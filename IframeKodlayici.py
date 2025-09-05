# ! Bu araç @keyiflerolsun tarafından | @KekikAkademi için yazılmıştır.

from Kekik.cli    import konsol
from cloudscraper import CloudScraper
import re, base64

class IframeKodlayici:
    @staticmethod
    def ters_cevir(metin: str) -> str:
        return metin[::-1]

    @staticmethod
    def base64_coz(encoded_string: str) -> str:
        # Base64 decoding için padding ekleme
        padding = len(encoded_string) % 4
        if padding:
            encoded_string += '=' * (4 - padding)
        return base64.b64decode(encoded_string).decode("utf-8")

    @staticmethod
    def iframe_parse(html_icerik: str) -> str:
        iframe_pattern = r'<iframe[^>]+src=["\']([^"\']+)["\'][^>]*>'
        match = re.search(iframe_pattern, html_icerik)
        return match.group(1) if match else None

    def iframe_coz(self, veri: str) -> str:
        # Ön ek kontrolü ve ekleme
        if not veri.startswith("PGltZyB3aWR0aD0iMTAwJSIgaGVpZ2"):
            on_ek = self.ters_cevir("BSZtFmcmlGP")
            veri = on_ek + veri

        try:
            # Base64 decode
            iframe_html = self.base64_coz(veri)
            # Iframe src'sini çıkar
            return self.iframe_parse(iframe_html)
        except Exception as e:
            konsol.log(f"[red]Çözümleme hatası: {e}[/red]")
            return None

# CloudScraper ile oturum oluştur
oturum = CloudScraper()

try:
    # Sayfayı getir
    istek = oturum.get("https://dizipub.club/the-twelve-3-sezon-5-bolum")
    istek.raise_for_status()  # HTTP hatalarını kontrol et
    
    # pdata değişkenlerini regex ile bul
    partlar = re.findall(r"pdata\[\'(.*?)\'\]\s*=\s*\'(.*?)\';", istek.text)
    
    if not partlar:
        konsol.log("[yellow]pdata bulunamadı! Sayfa yapısı değişmiş olabilir.[/yellow]")
        # Alternatif arama pattern'i
        partlar = re.findall(r"pdata\[['\"](.*?)['\"]\]\s*=\s*['\"](.*?)['\"];", istek.text)
    
    if partlar:
        kodlayici = IframeKodlayici()
        
        konsol.log(f"[green]Bulunan {len(partlar)} parça:[/green]")
        for parca_id, parca_veri in partlar:
            iframe = kodlayici.iframe_coz(parca_veri)
            if iframe:
                konsol.log(f"{parca_id:<6} » {iframe}")
            else:
                konsol.log(f"{parca_id:<6} » [red]Çözülemedi[/red]")
    else:
        konsol.log("[red]Hiç pdata bulunamadı![/red]")
        # Hata ayıklama için sayfa içeriğini incele
        if "pdata" in istek.text:
            konsol.log("[yellow]pdata kelimesi var ama regex eşleşmedi[/yellow]")
            
except Exception as e:
    konsol.log(f"[red]Hata oluştu: {e}[/red]")