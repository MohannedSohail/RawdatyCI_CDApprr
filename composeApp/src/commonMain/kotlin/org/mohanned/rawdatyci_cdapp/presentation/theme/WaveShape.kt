package org.mohanned.rawdatyci_cdapp.presentation.theme

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

/**
 * A custom shape that creates a convex wave effect at the bottom of a container.
 * Specifically used for the premium headers in the Rawdaty design system.
 */
class WaveShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height - 40f) // Start wave 40px from bottom
            
            // Create a quadratic Bézier curve for the wave (convex)
            quadraticTo(
                size.width / 2, size.height + 20f, // Control point below the height
                0f, size.height - 40f // Return point
            )
            
            close()
        }
        return Outline.Generic(path)
    }
}
