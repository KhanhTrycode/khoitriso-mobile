package com.example.khoitriso.utils.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val ArrowBigUp: ImageVector
    get() {
        if (_ArrowBigUp != null) return _ArrowBigUp!!

        _ArrowBigUp = ImageVector.Builder(
            name = "ArrowBigUp",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(

                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(9f, 18f)
                verticalLineToRelative(-6f)
                horizontalLineTo(5f)
                lineToRelative(7f, -7f)
                lineToRelative(7f, 7f)
                horizontalLineToRelative(-4f)
                verticalLineToRelative(6f)
                close()
            }
        }.build()

        return _ArrowBigUp!!
    }

private var _ArrowBigUp: ImageVector? = null

val ArrowBigDown: ImageVector
    get() {
        if (_ArrowBigDown != null) return _ArrowBigDown!!

        _ArrowBigDown = ImageVector.Builder(
            name = "ArrowBigDown",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(15f, 6f)
                verticalLineToRelative(6f)
                horizontalLineToRelative(4f)
                lineToRelative(-7f, 7f)
                lineToRelative(-7f, -7f)
                horizontalLineToRelative(4f)
                verticalLineTo(6f)
                close()
            }
        }.build()

        return _ArrowBigDown!!
    }

private var _ArrowBigDown: ImageVector? = null

