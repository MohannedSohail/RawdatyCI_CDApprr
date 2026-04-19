package org.mohanned.rawdatyci_cdapp.core.network

object ApiConfig {
    private const val SERVER_IP = "10.10.10.74"

    // private const val SERVER_IP = "192.168.1.109"
    var BASE_URL: String = "http://$SERVER_IP:8000/api/v1/"
    var tenantSlug: String = "demo"

    fun setTenant(slug: String) {
        tenantSlug = slug
    }
}
