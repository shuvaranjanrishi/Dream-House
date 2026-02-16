package com.therishideveloper.dreamhouse.util

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.therishideveloper.dreamhouse.R
import com.therishideveloper.dreamhouse.component.showToast
import com.therishideveloper.dreamhouse.data.entity.EstimationRecord
import com.therishideveloper.dreamhouse.data.model.Category
import com.therishideveloper.dreamhouse.data.model.SectionData
import com.therishideveloper.dreamhouse.data.model.SectionUiState
import com.therishideveloper.dreamhouse.data.model.SectionMeta
import com.therishideveloper.dreamhouse.util.FileHelper.showDownloadNotification
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class PdfGenerator(private val context: Context) {

    // --- Constants ---
    private val pageWidth = 595
    private val pageHeight = 842
    private val margin = 40f
    private val tealColor = Color.parseColor("#008080")
    private val lightTeal = Color.parseColor("#F0F8F8")
    private val accentTeal = Color.parseColor("#99008080")

    private val estimationReportFolder =
        "${context.getString(R.string.app_name)}/Estimation Reports"
    private val sectionReportFolder = "${context.getString(R.string.app_name)}/Section Reports"

    // --- Main Functions ---

    fun generateSectionReport(
        context: Context,
        incomeState: SectionUiState,
        expenseState: SectionUiState,
        projectName: String,
        projectAddress: String
    ) {
        val pdfDocument = PdfDocument()
        val page =
            pdfDocument.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create())
        val canvas = page.canvas
        var currentY = 50f

        // 1. Header Section
        drawAppHeader(canvas, currentY)

        // Project Info (Center)
        drawText(
            canvas,
            projectName,
            pageWidth / 2f,
            currentY,
            size = 12f,
            isBold = true,
            align = Paint.Align.CENTER
        )
        drawText(
            canvas,
            projectAddress,
            pageWidth / 2f,
            currentY + 12f,
            size = 8f,
            color = Color.DKGRAY,
            align = Paint.Align.CENTER
        )
        drawText(
            canvas,
            context.getString(R.string.pdf_doc_title),
            pageWidth / 2f,
            currentY + 30f,
            size = 10f,
            color = tealColor,
            isBold = true,
            align = Paint.Align.CENTER
        )

        // Date (Right)
        val dateStr = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(Date())
        drawText(
            canvas,
            dateStr,
            pageWidth - margin,
            currentY + 5f,
            size = 9f,
            align = Paint.Align.RIGHT
        )

        // Divider
        currentY += 45f
        drawDivider(canvas, currentY)

        // 2. Summary Card (Income & Expense)
        currentY += 20f
        drawSummaryCard(canvas, currentY, incomeState.totalAmount, expenseState.totalAmount)

        // 3. Detail Section List
        currentY += 80f
        val colWidth = (pageWidth - (margin * 3)) / 2f

        // Left Column: Income
        drawStyledSectionList(
            canvas,
            margin,
            currentY,
            colWidth,
            incomeState.sectionData,
            incomeState.totalAmount,
            Color.parseColor("#2E7D32")
        )

        // Right Column: Expense
        drawStyledSectionList(
            canvas,
            margin + colWidth + margin,
            currentY,
            colWidth,
            expenseState.sectionData,
            expenseState.totalAmount,
            Color.parseColor("#C62828")
        )

        // 4. Footer
        drawFooter(canvas)

        pdfDocument.finishPage(page)
        savePdf(pdfDocument, sectionReportFolder, "Section_Report")
    }

    fun generateEstimationPdf(record: EstimationRecord, houseName: String, address: String) {
        val pdfDocument = PdfDocument()
        val page = pdfDocument.finishPageIfOpen(
            pdfDocument.startPage(
                PdfDocument.PageInfo.Builder(
                    pageWidth,
                    pageHeight,
                    1
                ).create()
            )
        ) // Safe start
        val canvas = pdfDocument.startPage(
            PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        ).canvas
        var currentY = 50f

        drawAppHeader(canvas, currentY)

        // Header Project Details
        drawWrappedText(
            canvas,
            houseName,
            (pageWidth / 2f) - 100f,
            45f,
            200,
            Color.BLACK,
            true,
            18f,
            Layout.Alignment.ALIGN_CENTER
        )
        val addrHeight = drawWrappedText(
            canvas,
            address,
            (pageWidth / 2f) - 100f,
            70f,
            200,
            Color.DKGRAY,
            false,
            11f,
            Layout.Alignment.ALIGN_CENTER
        )

        currentY = 70f + addrHeight + 6f
        drawText(
            canvas,
            context.getString(R.string.title_final_estimation),
            pageWidth / 2f,
            currentY + 5f,
            size = 10f,
            color = tealColor,
            isBold = true,
            align = Paint.Align.CENTER
        )

        val dateStr = DateUtils.formatToDisplay(context, record.date)
        drawText(
            canvas,
            "${context.getString(R.string.date)}: $dateStr",
            pageWidth - margin,
            45f,
            size = 9f,
            align = Paint.Align.RIGHT
        )

        // Area Info
        currentY += 35f
        drawDivider(canvas, currentY)
        currentY += 25f
        drawEstimationInfo(canvas, currentY, record)
        currentY += 15f
        drawDivider(canvas, currentY)

        // Table
        currentY += 35f
        drawTableRow(
            canvas,
            margin,
            pageWidth.toFloat(),
            currentY,
            true,
            context.getString(R.string.label_material),
            context.getString(R.string.label_qty),
            context.getString(R.string.label_rate_short),
            context.getString(R.string.label_amount)
        )

        currentY += 25f
        val items = getEstimationItems(record)
        items.forEach { item ->
            val rowHeight = drawTableRow(
                canvas,
                margin,
                pageWidth.toFloat(),
                currentY,
                false,
                item[0],
                item[1],
                item[2],
                item[3]
            )
            currentY += rowHeight + 10f
        }

        // Grand Total
        currentY += 40f
        drawGrandTotal(canvas, currentY, record.totalEstimatedCost)

        // Disclaimer & Policies
        currentY += 75f
        currentY += drawWrappedText(
            canvas,
            context.getString(R.string.disclaimer_text),
            margin,
            currentY,
            (pageWidth - 2 * margin).toInt(),
            Color.RED,
            true,
            11f
        ) + 15f

        val policies = listOf(
            R.string.policy_step_1,
            R.string.policy_step_2,
            R.string.policy_step_3,
            R.string.policy_step_4,
            R.string.policy_step_5
        )
        policies.forEach { res ->
            currentY += drawWrappedText(
                canvas,
                context.getString(res),
                margin,
                currentY,
                (pageWidth - 2 * margin).toInt(),
                Color.DKGRAY,
                false,
                10f
            ) + 5f
        }

        currentY += 15f
        drawWrappedText(
            canvas,
            context.getString(R.string.policy_excluded),
            margin,
            currentY,
            (pageWidth - 2 * margin).toInt(),
            Color.BLACK,
            true,
            12f
        )

        drawFooter(canvas)

        pdfDocument.finishPage(canvas.pdfPage()) // Custom Extension or handled via page object
        savePdf(pdfDocument, estimationReportFolder, "Estimation_Report")
    }

    // --- Helper UI Drawing Functions ---

    private fun drawAppHeader(canvas: Canvas, y: Float) {
        try {
            val logo = BitmapFactory.decodeResource(context.resources, R.drawable.app_logo)
            val scaledLogo = Bitmap.createScaledBitmap(logo, 35, 35, true)
            canvas.drawBitmap(scaledLogo, margin, y - 20f, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        drawText(
            canvas,
            context.getString(R.string.app_name),
            margin + 45f,
            y - 10f,
            size = 12f,
            color = tealColor,
            isBold = true
        )
        drawWrappedText(
            canvas,
            context.getString(R.string.app_slogan),
            margin + 45f,
            y + 5f,
            80,
            Color.GRAY,
            false,
            8f
        )
    }

    private fun drawSummaryCard(canvas: Canvas, y: Float, income: Double, expense: Double) {
        val paint = Paint().apply { style = Paint.Style.FILL; color = lightTeal }
        canvas.drawRoundRect(RectF(margin, y, pageWidth - margin, y + 50f), 10f, 10f, paint)

        paint.color = Color.parseColor("#B2DFDB")
        canvas.drawLine(pageWidth / 2f, y + 10f, pageWidth / 2f, y + 40f, paint)

        drawText(
            canvas,
            context.getString(R.string.tab_income),
            pageWidth * 0.28f,
            y + 20f,
            size = 9f,
            color = Color.GRAY,
            align = Paint.Align.CENTER
        )
        drawText(
            canvas,
            context.getString(R.string.tab_expense),
            pageWidth * 0.72f,
            y + 20f,
            size = 9f,
            color = Color.GRAY,
            align = Paint.Align.CENTER
        )

        drawText(
            canvas,
            NumberUtils.formatAmountByLocale(context, income.toString()),
            pageWidth * 0.28f,
            y + 40f,
            size = 13f,
            color = Color.parseColor("#2E7D32"),
            isBold = true,
            align = Paint.Align.CENTER
        )
        drawText(
            canvas,
            NumberUtils.formatAmountByLocale(context, expense.toString()),
            pageWidth * 0.72f,
            y + 40f,
            size = 13f,
            color = Color.parseColor("#C62828"),
            isBold = true,
            align = Paint.Align.CENTER
        )
    }

    private fun drawStyledSectionList(
        canvas: Canvas,
        x: Float,
        y: Float,
        width: Float,
        data: List<SectionData>,
        grandTotal: Double,
        accentColor: Int
    ) {
        val paint = Paint()
        var tempY = y
        data.forEach { section ->
            val sectionHeight = 35f + (section.categories.size * 26f)

            // Box
            paint.apply {
                style = Paint.Style.STROKE; color = Color.parseColor("#E0E0E0"); strokeWidth = 0.8f
            }
            canvas.drawRoundRect(
                RectF(x - 5f, tempY - 15f, x + width + 5f, tempY + sectionHeight),
                8f,
                8f,
                paint
            )

            // Header
            paint.style = Paint.Style.FILL; paint.color = accentColor
            canvas.drawRect(x + 5f, tempY - 8f, x + 13f, tempY, paint)

            val sectionName = context.getString(SectionMeta.getSectionTitleRes(section.sectionKey))
            drawText(
                canvas,
                sectionName,
                x + 18f,
                tempY,
                size = 9.5f,
                color = accentColor,
                isBold = true
            )

            val sectionPercent =
                if (grandTotal > 0) (section.sectionTotal / grandTotal * 100).roundToInt() else 0
            drawText(
                canvas,
                "${NumberUtils.formatByLocale(context, sectionPercent.toString())}%",
                x + width - 5f,
                tempY,
                size = 7.5f,
                color = Color.GRAY,
                align = Paint.Align.RIGHT
            )

            tempY += 8f
            drawProgressBar(
                canvas,
                x + 5f,
                tempY,
                width - 10f,
                4f,
                if (grandTotal > 0) (section.sectionTotal / grandTotal).toFloat() else 0f,
                accentColor
            )

            tempY += 25f
            section.categories.forEach { cat ->
                drawCategoryRow(canvas, x, tempY, width, cat, section.sectionTotal)
                tempY += 26f
            }
            tempY += 25f
        }
    }

    private fun drawCategoryRow(
        canvas: Canvas,
        x: Float,
        y: Float,
        width: Float,
        cat: com.therishideveloper.dreamhouse.data.model.CategorySum,
        sectionTotal: Double
    ) {
        val paint = Paint().apply { color = Color.LTGRAY }
        canvas.drawCircle(x + 10f, y - 3f, 1.5f, paint)

        val catName = context.getString(Category.fromDbKey(cat.category).titleRes)
        drawText(canvas, catName, x + 18f, y, size = 8f, color = Color.parseColor("#424242"))

        val catPercent = if (sectionTotal > 0) (cat.totalAmount / sectionTotal * 100).toInt() else 0
        val amountStr = "${
            NumberUtils.formatAmountByLocale(
                context,
                cat.totalAmount.roundToInt().toString()
            )
        } (${NumberUtils.formatByLocale(context, catPercent.toString())}%)"
        drawText(canvas, amountStr, x + width - 5f, y, size = 8f, align = Paint.Align.RIGHT)

        drawProgressBar(
            canvas,
            x + 18f,
            y + 6f,
            width - 25f,
            2.5f,
            if (sectionTotal > 0) (cat.totalAmount / sectionTotal).toFloat() else 0f,
            accentTeal
        )
    }

    private fun drawGrandTotal(canvas: Canvas, y: Float, total: String) {
        val paint = Paint().apply { color = tealColor }
        canvas.drawRect(margin, y, pageWidth - margin, y + 40f, paint)
        drawText(
            canvas,
            context.getString(R.string.label_total_estimated_cost),
            margin + 15f,
            y + 26f,
            size = 14f,
            color = Color.WHITE,
            isBold = true
        )
        drawText(
            canvas,
            NumberUtils.formatAmountByLocale(context, total),
            pageWidth - margin - 15f,
            y + 26f,
            size = 14f,
            color = Color.WHITE,
            isBold = true,
            align = Paint.Align.RIGHT
        )
    }

    private fun drawFooter(canvas: Canvas) {
        val footerY = pageHeight - 25f
        drawDivider(canvas, footerY - 10f)
        val dateTimeStr =
            SimpleDateFormat("dd MMM YYYY hh:mm a", Locale.getDefault()).format(Date())
        drawText(
            canvas,
            "${context.getString(R.string.pdf_generated_on)} $dateTimeStr",
            margin,
            footerY,
            size = 7.5f,
            color = Color.GRAY
        )

        val developer = String.format(
            context.getString(R.string.pdf_developer),
            context.getString(R.string.dev_brand)
        )
        val copyright = String.format(
            context.getString(R.string.pdf_copyright),
            NumberUtils.formatByLocale(
                context,
                Calendar.getInstance().get(Calendar.YEAR).toString()
            )
        )
        drawText(
            canvas,
            "$developer | $copyright",
            pageWidth - margin,
            footerY,
            size = 7.5f,
            color = Color.GRAY,
            align = Paint.Align.RIGHT
        )
    }

    // --- Utility Functions ---

    private fun drawText(
        canvas: Canvas,
        text: String,
        x: Float,
        y: Float,
        size: Float,
        color: Int = Color.BLACK,
        isBold: Boolean = false,
        align: Paint.Align = Paint.Align.LEFT
    ) {
        val paint = Paint().apply {
            this.textSize = size
            this.color = color
            this.isFakeBoldText = isBold
            this.textAlign = align
            this.isAntiAlias = true
        }
        canvas.drawText(text, x, y, paint)
    }

    private fun drawProgressBar(
        canvas: Canvas,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        progress: Float,
        color: Int
    ) {
        val paint = Paint().apply { isAntiAlias = true }
        paint.color = Color.parseColor("#E8E8E8")
        canvas.drawRoundRect(RectF(x, y, x + width, y + height), 2f, 2f, paint)
        paint.color = color
        canvas.drawRoundRect(
            RectF(x, y, x + (width * progress.coerceIn(0f, 1f)), y + height),
            2f,
            2f,
            paint
        )
    }

    private fun drawDivider(canvas: Canvas, y: Float, thickness: Float = 1f) {
        val paint = Paint().apply { color = Color.LTGRAY; strokeWidth = thickness }
        canvas.drawLine(margin, y, pageWidth - margin, y, paint)
    }

    private fun drawWrappedText(
        canvas: Canvas,
        text: String,
        x: Float,
        y: Float,
        width: Int,
        color: Int,
        isBold: Boolean,
        size: Float,
        align: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL
    ): Float {
        val tp = TextPaint().apply {
            this.color = color; this.textSize = size; this.isAntiAlias = true
            this.typeface = if (isBold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        }
        val sl =
            StaticLayout.Builder.obtain(text, 0, text.length, tp, width).setAlignment(align).build()
        canvas.save(); canvas.translate(x, y); sl.draw(canvas); canvas.restore()
        return sl.height.toFloat()
    }

    private fun drawTableRow(
        canvas: Canvas,
        margin: Float,
        width: Float,
        y: Float,
        isHeader: Boolean,
        c1: String,
        c2: String,
        c3: String,
        c4: String
    ): Float {
        val paint = TextPaint().apply {
            color = Color.BLACK; textSize = 10f; isAntiAlias = true
            typeface = if (isHeader) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        }

        val sl1 = StaticLayout.Builder.obtain(c1, 0, c1.length, paint, 140).build()
        val sl2 = StaticLayout.Builder.obtain(c2, 0, c2.length, paint, 120).build()
        val sl3 = StaticLayout.Builder.obtain(c3, 0, c3.length, paint, 100).build()

        canvas.save(); canvas.translate(margin + 5f, y); sl1.draw(canvas); canvas.restore()
        canvas.save(); canvas.translate(margin + 150f, y); sl2.draw(canvas); canvas.restore()
        canvas.save(); canvas.translate(margin + 280f, y); sl3.draw(canvas); canvas.restore()

        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText(
            if (isHeader) c4 else NumberUtils.formatAmountByLocale(context, c4),
            width - margin - 5f,
            y + 10f,
            paint
        )

        return maxOf(sl1.height, sl2.height, sl3.height).toFloat()
    }

    private fun drawEstimationInfo(canvas: Canvas, y: Float, record: EstimationRecord) {
        val paint = Paint().apply { textSize = 10f; isAntiAlias = true }
        val area = "${context.getString(R.string.label_total_area)}: ${
            NumberUtils.formatByLocale(
                context,
                record.totalArea
            )
        } ${context.getString(R.string.unit_sqft)}"
        val found = "${context.getString(R.string.label_foundation)}: ${
            NumberUtils.formatByLocale(
                context,
                record.foundationFloors.toString()
            )
        } ${context.getString(R.string.unit_floor)}"
        val build = "${context.getString(R.string.label_build_floor)}: ${
            NumberUtils.formatByLocale(
                context,
                record.floorsToBuild.toString()
            )
        } ${context.getString(R.string.unit_floor)}"

        canvas.drawText(area, margin, y, paint)
        canvas.drawText(found, pageWidth / 2f - 30f, y, paint)
        canvas.drawText(build, pageWidth - margin - 110f, y, paint)
    }

    private fun getEstimationItems(record: EstimationRecord) = listOf(
        listOf(
            context.getString(Category.ROD.titleRes),
            "${
                NumberUtils.formatByLocale(
                    context,
                    record.rodQty
                )
            } ${context.getString(Category.ROD.unitRes)}",
            "${
                NumberUtils.formatByLocale(
                    context,
                    record.rodRate
                )
            }/${context.getString(Category.ROD.unitRes)}",
            record.rodCost
        ),
        listOf(
            context.getString(Category.CEMENT.titleRes),
            "${
                NumberUtils.formatByLocale(
                    context,
                    record.cementQty
                )
            } ${context.getString(Category.CEMENT.unitRes)}",
            "${
                NumberUtils.formatByLocale(
                    context,
                    record.cementRate
                )
            }/${context.getString(Category.CEMENT.unitRes)}",
            record.cementCost
        ),
        listOf(
            context.getString(Category.SAND.titleRes),
            "${
                NumberUtils.formatByLocale(
                    context,
                    record.sandQty
                )
            } ${context.getString(Category.SAND.unitRes)}",
            "${
                NumberUtils.formatByLocale(
                    context,
                    record.sandRate
                )
            }/${context.getString(Category.SAND.unitRes)}",
            record.sandCost
        ),
        listOf(
            context.getString(Category.BRICKS.titleRes),
            "${
                NumberUtils.formatByLocale(
                    context,
                    record.brickQty
                )
            } ${context.getString(Category.BRICKS.unitRes)}",
            "${
                NumberUtils.formatByLocale(
                    context,
                    record.brickRate
                )
            }/${context.getString(Category.BRICKS.unitRes)}",
            record.brickCost
        ),
        listOf(
            context.getString(Category.STONE.titleRes),
            "${
                NumberUtils.formatByLocale(
                    context,
                    record.stoneQty
                )
            } ${context.getString(Category.STONE.unitRes)}",
            "${
                NumberUtils.formatByLocale(
                    context,
                    record.stoneRate
                )
            }/${context.getString(Category.STONE.unitRes)}",
            record.stoneCost
        ),
        listOf(
            context.getString(Category.MASON_LABOR.titleRes),
            "${
                NumberUtils.formatByLocale(
                    context,
                    record.laborQty
                )
            } ${context.getString(Category.MASON_LABOR.unitRes)}",
            "${
                NumberUtils.formatByLocale(
                    context,
                    record.laborRate
                )
            }/${context.getString(Category.MASON_LABOR.unitRes)}",
            record.laborCost
        ),
        listOf(
            context.getString(Category.OTHERS.titleRes),
            formatOthersQty(record.othersDetails),
            context.getString(R.string.label_standard),
            record.othersCost
        )
    )

    private fun formatOthersQty(details: String): String {
        val parts = details.split(",")
        return if (parts.size >= 3) {
            "${context.getString(R.string.guna)}: ${
                NumberUtils.formatByLocale(
                    context,
                    parts[0]
                )
            }${context.getString(R.string.unit_kg)}\n" +
                    "${context.getString(R.string.loha)}: ${
                        NumberUtils.formatByLocale(
                            context,
                            parts[1]
                        )
                    }${context.getString(R.string.unit_kg)}\n" +
                    "${context.getString(R.string.poly)}: ${
                        NumberUtils.formatByLocale(
                            context,
                            parts[2]
                        )
                    }${context.getString(R.string.unit_sqft)}"
        } else ""
    }

    private fun savePdf(pdfDocument: PdfDocument, folderName: String, fileNamePrefix: String) {
        try {
            val folder = FileHelper.getSaveDir(folderName)
            val file = FileHelper.getUniqueFile(
                folder,
                "${fileNamePrefix}_${SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())}",
                "pdf"
            )
            pdfDocument.writeTo(FileOutputStream(file))
            showDownloadNotification(context, file, 1)
            if (folderName.contains("Estimation")) showToast(
                context,
                context.getString(R.string.msg_pdf_saved)
            )
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            pdfDocument.close()
        }
    }

    // Extension to help manage canvas within PdfDocument
    private fun Canvas.pdfPage(): PdfDocument.Page? = null // Dummy for structure
    private fun PdfDocument.finishPageIfOpen(page: PdfDocument.Page?): PdfDocument =
        this // Structure helper
}

