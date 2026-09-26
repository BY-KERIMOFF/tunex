import os
from typing import List
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    APP_NAME: str = "Neo Radio API"
    APP_VERSION: str = "1.0.0"
    DEBUG: bool = False
    HOST: str = "0.0.0.0"
    PORT: int = 8000
    BASE_URL: str = "https://3bneoplay65.xyz/radio"

    RADIO_BROWSER_MIRRORS: str = (
        "https://de1.api.radio-browser.info,"
        "https://nl1.api.radio-browser.info,"
        "https://at1.api.radio-browser.info,"
        "https://fi1.api.radio-browser.info"
    )
    RADIO_BROWSER_PAGE_SIZE: int = 500
    RADIO_BROWSER_MAX_PAGES: int = 100
    RADIO_BROWSER_TIMEOUT: int = 30
    RADIO_BROWSER_USER_AGENT: str = "NeoRadio/1.0"

    CACHE_TYPE: str = "file"
    CACHE_FILE_PATH: str = "./data/stations.json"
    CACHE_TTL_SECONDS: int = 21600
    REDIS_URL: str = "redis://localhost:6379/0"

    GO_PROXY_HOST: str = "127.0.0.1"
    GO_PROXY_PORT: int = 8001
    GO_PROXY_TIMEOUT: int = 20
    GO_PROXY_MAX_CONNECTIONS: int = 1000

    CORS_ORIGINS: str = "*"
    RATE_LIMIT_PER_MINUTE: int = 60
    ADMIN_TOKEN: str = "change_me_in_production"

    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        extra="ignore"
    )

    @property
    def mirror_list(self) -> List[str]:
        return [m.strip() for m in self.RADIO_BROWSER_MIRRORS.split(",") if m.strip()]

    @property
    def cors_origins_list(self) -> List[str]:
        if self.CORS_ORIGINS == "*":
            return ["*"]
        return [o.strip() for o in self.CORS_ORIGINS.split(",") if o.strip()]


settings = Settings()
