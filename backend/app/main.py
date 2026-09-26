import logging
from contextlib import asynccontextmanager
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from .config import settings
from .routers import health, m3u, stations, search, stream
from .services.radio_browser import radio_browser_service

logging.basicConfig(
    level=logging.INFO if not settings.DEBUG else logging.DEBUG,
    format="%(asctime)s - %(name)s - %(levelname)s - %(message)s"
)
logger = logging.getLogger("neo_radio")


@asynccontextmanager
async def lifespan(app: FastAPI):
    logger.info("Starting up Neo Radio API...")
    try:
        await radio_browser_service.fetch_all_stations()
    except Exception as e:
        logger.error(f"Error fetching initial stations on startup: {e}")
    yield
    logger.info("Shutting down Neo Radio API...")


app = FastAPI(
    title=settings.APP_NAME,
    version=settings.APP_VERSION,
    lifespan=lifespan
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.cors_origins_list,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(health.router)
app.include_router(m3u.router)
app.include_router(stations.router)
app.include_router(search.router)
app.include_router(stream.router)


if __name__ == "__main__":
    import uvicorn
    uvicorn.run("app.main:app", host=settings.HOST, port=settings.PORT, reload=settings.DEBUG)
