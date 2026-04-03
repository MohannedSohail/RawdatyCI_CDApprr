@file:OptIn(InternalResourceApi::class)

package rawdatyci_cdapp.composeapp.generated.resources

import kotlin.OptIn
import kotlin.String
import kotlin.collections.MutableMap
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.ResourceContentHash
import org.jetbrains.compose.resources.ResourceItem

private const val MD: String = "composeResources/rawdatyci_cdapp.composeapp.generated.resources/"

@delegate:ResourceContentHash(1_934_606_848)
internal val Res.drawable.onboarding: DrawableResource by lazy {
      DrawableResource("drawable:onboarding", setOf(
        ResourceItem(setOf(), "${MD}drawable/onboarding.png", -1, -1),
      ))
    }

@delegate:ResourceContentHash(-1_779_049_636)
internal val Res.drawable.onboarding1: DrawableResource by lazy {
      DrawableResource("drawable:onboarding1", setOf(
        ResourceItem(setOf(), "${MD}drawable/onboarding1.png", -1, -1),
      ))
    }

@delegate:ResourceContentHash(-1_178_458_685)
internal val Res.drawable.onboarding2: DrawableResource by lazy {
      DrawableResource("drawable:onboarding2", setOf(
        ResourceItem(setOf(), "${MD}drawable/onboarding2.png", -1, -1),
      ))
    }

@delegate:ResourceContentHash(-1_537_875_007)
internal val Res.drawable.rawdatylogo: DrawableResource by lazy {
      DrawableResource("drawable:rawdatylogo", setOf(
        ResourceItem(setOf(), "${MD}drawable/rawdatylogo.png", -1, -1),
      ))
    }

@InternalResourceApi
internal fun _collectCommonMainDrawable0Resources(map: MutableMap<String, DrawableResource>) {
  map.put("onboarding", Res.drawable.onboarding)
  map.put("onboarding1", Res.drawable.onboarding1)
  map.put("onboarding2", Res.drawable.onboarding2)
  map.put("rawdatylogo", Res.drawable.rawdatylogo)
}
