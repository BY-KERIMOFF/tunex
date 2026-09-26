import pytest
from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)


def test_health_check():
    response = client.get("/health")
    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "ok"
    assert "uptime_seconds" in data


def test_root_info():
    response = client.get("/")
    assert response.status_code == 200
    data = response.json()
    assert "app_name" in data
    assert "version" in data


def test_m3u_endpoint():
    response = client.get("/m3u?limit=5")
    assert response.status_code == 200
    assert response.headers["content-type"].startswith("audio/x-mpegurl")
    assert "#EXTM3U" in response.text


def test_stream_redirect():
    target_url = "http://example.com/stream.mp3"
    response = client.get(f"/stream?url={target_url}", follow_redirects=False)
    assert response.status_code == 307
    assert "/stream?url=" in response.headers["location"]
