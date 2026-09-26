package main

import (
	"context"
	"fmt"
	"io"
	"log"
	"net"
	"net/http"
	"os"
	"os/signal"
	"sync/atomic"
	"syscall"
	"time"
)

var (
	activeConnections int64
	totalRequests     int64
	failedRequests    int64
)

func getEnv(key, fallback string) string {
	if val := os.Getenv(key); val != "" {
		return val
	}
	return fallback
}

func healthHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	fmt.Fprintf(w, `{"status":"ok","active_connections":%d}`, atomic.LoadInt64(&activeConnections))
}

func metricsHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "text/plain; version=0.0.4")
	fmt.Fprintf(w, "# HELP neo_radio_active_connections Number of active stream proxy connections\n")
	fmt.Fprintf(w, "# TYPE neo_radio_active_connections gauge\n")
	fmt.Fprintf(w, "neo_radio_active_connections %d\n", atomic.LoadInt64(&activeConnections))

	fmt.Fprintf(w, "# HELP neo_radio_total_requests_total Total number of proxy stream requests\n")
	fmt.Fprintf(w, "# TYPE neo_radio_total_requests_total counter\n")
	fmt.Fprintf(w, "neo_radio_total_requests_total %d\n", atomic.LoadInt64(&totalRequests))

	fmt.Fprintf(w, "# HELP neo_radio_failed_requests_total Total number of failed proxy requests\n")
	fmt.Fprintf(w, "# TYPE neo_radio_failed_requests_total counter\n")
	fmt.Fprintf(w, "neo_radio_failed_requests_total %d\n", atomic.LoadInt64(&failedRequests))
}

func streamHandler(w http.ResponseWriter, r *http.Request) {
	atomic.AddInt64(&totalRequests, 1)
	streamURL := r.URL.Query().Get("url")
	if streamURL == "" {
		http.Error(w, "Missing 'url' parameter", http.StatusBadRequest)
		atomic.AddInt64(&failedRequests, 1)
		return
	}

	client := &http.Client{
		Timeout: 20 * time.Second,
		CheckRedirect: func(req *http.Request, via []*http.Request) error {
			if len(via) >= 10 {
				return fmt.Errorf("too many redirects")
			}
			return nil
		},
		Transport: &http.Transport{
			DialContext: (&net.Dialer{
				Timeout:   10 * time.Second,
				KeepAlive: 30 * time.Second,
			}).DialContext,
			MaxIdleConns:        1000,
			MaxIdleConnsPerHost: 100,
			IdleConnTimeout:     90 * time.Second,
		},
	}

	req, err := http.NewRequestWithContext(r.Context(), "GET", streamURL, nil)
	if err != nil {
		http.Error(w, "Failed to create request: "+err.Error(), http.StatusInternalServerError)
		atomic.AddInt64(&failedRequests, 1)
		return
	}

	req.Header.Set("User-Agent", "NeoRadio-GoProxy/1.0")
	req.Header.Set("Icy-MetaData", "1")

	resp, err := client.Do(req)
	if err != nil {
		http.Error(w, "Stream connection failed: "+err.Error(), http.StatusBadGateway)
		atomic.AddInt64(&failedRequests, 1)
		return
	}
	defer resp.Body.Close()

	for k, v := range resp.Header {
		for _, val := range v {
			w.Header().Add(k, val)
		}
	}

	w.WriteHeader(resp.StatusCode)

	atomic.AddInt64(&activeConnections, 1)
	defer atomic.AddInt64(&activeConnections, -1)

	buf := make([]byte, 32*1024)
	_, _ = io.CopyBuffer(w, resp.Body, buf)
}

func main() {
	host := getEnv("GO_PROXY_HOST", "0.0.0.0")
	port := getEnv("GO_PROXY_PORT", "8001")
	addr := fmt.Sprintf("%s:%s", host, port)

	mux := http.NewServeMux()
	mux.HandleFunc("/health", healthHandler)
	mux.HandleFunc("/metrics", metricsHandler)
	mux.HandleFunc("/stream", streamHandler)

	server := &http.Server{
		Addr:         addr,
		Handler:      mux,
		ReadTimeout:  15 * time.Second,
		WriteTimeout: 0, // Unbounded for live audio streaming
		IdleTimeout:  60 * time.Second,
	}

	stop := make(chan os.Signal, 1)
	signal.Notify(stop, os.Interrupt, syscall.SIGTERM)

	go func() {
		log.Printf("Go Stream Proxy starting on %s...", addr)
		if err := server.ListenAndServe(); err != nil && err != http.ErrServerClosed {
			log.Fatalf("Server failed: %v", err)
		}
	}()

	<-stop
	log.Println("Shutting down Go Stream Proxy gracefully...")

	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	if err := server.Shutdown(ctx); err != nil {
		log.Fatalf("Server forced to shutdown: %v", err)
	}

	log.Println("Go Stream Proxy stopped cleanly.")
}
