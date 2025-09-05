// ! Bu araç @keyiflerolsun tarafından | @KekikAkademi için yazılmıştır.

package com.keyiflerolsun

import android.util.Log
import com.lagradost.cloudstream3.ErrorLoadingException
import com.lagradost.cloudstream3.SubtitleFile
import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.utils.*

open class ContentX : ExtractorApi() {
    override val name            = "ContentX"
    override val mainUrl         = "https://contentx.me"
    override val requiresReferer = true

    // Her iki versiyonda da çalışan akıllı fonksiyon
    private fun createExtractorLink(
        source: String,
        name: String,
        url: String,
        referer: String,
        quality: Int,
        isM3u8: Boolean,
        headers: Map<String, String>
    ): ExtractorLink {
        return try {
            // Önce yeni yöntemi dene (yerel için)
            ExtractorLink(
                source = source,
                name = name,
                url = url,
                referer = referer,
                quality = quality,
                isM3u8 = isM3u8,
                headers = headers
            )
        } catch (e: Exception) {
            // Yeni yöntem başarısız olursa eski yöntemi kullan (GitHub için)
            newExtractorLink(
                source = source,
                name = name,
                url = url,
                type = if (isM3u8) ExtractorLinkType.M3U8 else ExtractorLinkType.VIDEO
            ) {
                this.headers = headers
                this.quality = quality
            }
        }
    }

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val extRef = referer ?: mainUrl

        val iSource = app.get(url, referer = extRef).text
        val iExtract = Regex("""window\.openPlayer\('([^']+)'""").find(iSource)?.groups?.get(1)?.value 
            ?: throw ErrorLoadingException("iExtract is null")

        val subUrls = mutableSetOf<String>()
        Regex(""""file":"((?:\\\\\"|[^"])+)","label":"((?:\\\\\"|[^"])+)"""").findAll(iSource).forEach {
            val (subUrlExt, subLangExt) = it.destructured

            val subUrl = subUrlExt.replace("\\/", "/").replace("\\u0026", "&").replace("\\", "")
            val subLang = subLangExt.replace("\\u0131", "ı").replace("\\u0130", "İ")
                .replace("\\u00fc", "ü").replace("\\u00e7", "ç")
                .replace("\\u011f", "ğ").replace("\\u015f", "ş")
        
            Log.d("ContentX_Subtitle", "$subLang » $subUrl")    
            if (subUrl in subUrls) return@forEach
            subUrls.add(subUrl)

            subtitleCallback.invoke(
                SubtitleFile(
                    lang = subLang,
                    url = fixUrl(subUrl)
                )
            )
        }

        val vidSource = app.get("${mainUrl}/source2.php?v=${iExtract}", referer = extRef).text
        val vidExtract = Regex("""file":"([^"]+)""").find(vidSource)?.groups?.get(1)?.value 
            ?: throw ErrorLoadingException("vidExtract is null")
        val m3uLink = vidExtract.replace("\\", "")

        // Her iki ortamda çalışan ortak fonksiyon
        callback.invoke(
            createExtractorLink(
                source = name,
                name = name,
                url = m3uLink,
                referer = url,
                quality = Qualities.Unknown.value,
                isM3u8 = true,
                headers = mapOf(
                    "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36 Norton/124.0.0.0"
                )
            )
        )

        Log.d("ContentX_Debug", "url » $url")
        
        val iDublaj = Regex("""","([^']+)","Türkçe""").find(iSource)?.groups?.get(1)?.value
        if (iDublaj != null) {
            try {
                val dublajSource = app.get("${mainUrl}/source2.php?v=${iDublaj}", referer = extRef).text
                val dublajExtract = Regex("""file":"([^"]+)""").find(dublajSource)?.groups?.get(1)?.value 
                    ?: throw ErrorLoadingException("dublajExtract is null")
                val dublajLink = dublajExtract.replace("\\", "")

                // Her iki ortamda çalışan ortak fonksiyon
                callback.invoke(
                    createExtractorLink(
                        source = "$name Türkçe Dublaj",
                        name = "$name Türkçe Dublaj", 
                        url = dublajLink,
                        referer = url,
                        quality = Qualities.Unknown.value,
                        isM3u8 = true,
                        headers = mapOf(
                            "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36 Norton/124.0.0.0"
                        )
                    )
                )
            } catch (e: Exception) {
                Log.e("ContentX_Error", "Dublaj linki alınırken hata: ${e.message}")
            }
        }
    }
}