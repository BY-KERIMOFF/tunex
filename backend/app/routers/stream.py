from urllib.parse import quote
from fastapi import APIRouter, Query, HTTPException
from fastapi.responses import RedirectResponse
from ..config import settings

router = APIRouter()


@router.get("/stream")
async def proxy_stream(url: str = Query(..., description="Target radio stream URL")):
    if not url:
        raise HTTPException(status_code=400, detail="URL parameter is required")

    encoded_target_url = quote(url, safe="")
    go_proxy_url = f"http://{settings.GO_PROXY_HOST}:{settings.GO_PROXY_PORT}/stream?url={encoded_target_url}"
    return RedirectResponse(url=go_proxy_url, status_code=307)
