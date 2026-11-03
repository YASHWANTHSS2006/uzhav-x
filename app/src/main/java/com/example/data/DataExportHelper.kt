package com.example.data

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.model.AppLanguage
import com.example.model.GovtScheme
import com.example.model.MandiPrice
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DataExportHelper {

    private const val TAG = "DataExportHelper"

    private fun getExportDirectory(context: Context): File {
        val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "UzhavX_Exports")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    private fun getTimestamp(): String {
        return SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    }

    private fun getReadableDate(): String {
        return SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
    }

    private fun escapeCsv(value: String): String {
        val clean = value.replace("\"", "\"\"")
        return "\"$clean\""
    }

    // =========================================================================
    // 1. CSV EXPORT FOR MANDI PRICES
    // =========================================================================
    fun exportMandiPricesCsv(context: Context, prices: List<MandiPrice>): File? {
        return try {
            val fileName = "Mandi_Prices_${getTimestamp()}.csv"
            val file = File(getExportDirectory(context), fileName)
            FileOutputStream(file).use { out ->
                // Write UTF-8 BOM so Excel opens Tamil characters properly
                out.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))
                val sb = StringBuilder()
                sb.append("ID,Crop Name,Tamil Name,Category,Market Name,District,Min Price (INR),Max Price (INR),Modal Price (INR),Unit,Arrival Volume,Last Updated\n")

                for (price in prices) {
                    sb.append("${escapeCsv(price.id)},")
                    sb.append("${escapeCsv(price.cropName)},")
                    sb.append("${escapeCsv(price.tamilName)},")
                    sb.append("${escapeCsv(price.category)},")
                    sb.append("${escapeCsv(price.marketName)},")
                    sb.append("${escapeCsv(price.district)},")
                    sb.append("${price.minPrice},")
                    sb.append("${price.maxPrice},")
                    sb.append("${price.modalPrice},")
                    sb.append("${escapeCsv(price.unit)},")
                    sb.append("${escapeCsv(price.arrivalVolume)},")
                    sb.append("${escapeCsv(price.lastUpdated)}\n")
                }
                out.write(sb.toString().toByteArray(Charsets.UTF_8))
            }
            Log.d(TAG, "Mandi prices exported to CSV: ${file.absolutePath}")
            file
        } catch (e: Exception) {
            Log.e(TAG, "CSV Export failed: ${e.message}", e)
            null
        }
    }

    // =========================================================================
    // 2. CSV EXPORT FOR GOVERNMENT SUBSIDIES
    // =========================================================================
    fun exportSubsidiesCsv(context: Context, schemes: List<GovtScheme>): File? {
        return try {
            val fileName = "Govt_Subsidies_${getTimestamp()}.csv"
            val file = File(getExportDirectory(context), fileName)
            FileOutputStream(file).use { out ->
                // Write UTF-8 BOM
                out.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))
                val sb = StringBuilder()
                sb.append("ID,Scheme Code,Scheme Title,Tamil Title,Category,Department,Subsidy %,Max Financial Benefit,Portal URL,Summary\n")

                for (scheme in schemes) {
                    sb.append("${escapeCsv(scheme.id)},")
                    sb.append("${escapeCsv(scheme.schemeCode)},")
                    sb.append("${escapeCsv(scheme.title)},")
                    sb.append("${escapeCsv(scheme.tamilTitle)},")
                    sb.append("${escapeCsv(scheme.category.name)},")
                    sb.append("${escapeCsv(scheme.department)},")
                    sb.append("${escapeCsv(scheme.subsidyPercentage)},")
                    sb.append("${escapeCsv(scheme.maxFinancialBenefit)},")
                    sb.append("${escapeCsv(scheme.officialPortalUrl)},")
                    sb.append("${escapeCsv(scheme.summaryEnglish)}\n")
                }
                out.write(sb.toString().toByteArray(Charsets.UTF_8))
            }
            Log.d(TAG, "Subsidies exported to CSV: ${file.absolutePath}")
            file
        } catch (e: Exception) {
            Log.e(TAG, "CSV Export failed: ${e.message}", e)
            null
        }
    }

    // =========================================================================
    // 3. PDF EXPORT FOR MANDI PRICES
    // =========================================================================
    fun exportMandiPricesPdf(context: Context, prices: List<MandiPrice>, language: AppLanguage): File? {
        return try {
            val pdfDoc = PdfDocument()
            val pageWidth = 595 // A4 width in points
            val pageHeight = 842 // A4 height in points
            var pageNumber = 1

            var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            var page = pdfDoc.startPage(pageInfo)
            var canvas = page.canvas

            val paint = Paint()
            val textPaint = Paint().apply { isAntiAlias = true }

            var startY = 40f

            fun drawHeader(c: Canvas) {
                // Top Green Header Banner
                paint.color = Color.parseColor("#1B5E20") // Dark Forest Green
                c.drawRect(0f, 0f, pageWidth.toFloat(), 65f, paint)

                textPaint.color = Color.WHITE
                textPaint.textSize = 18f
                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                val title = if (language == AppLanguage.TAMIL) "உழவன் எக்ஸ் - தினசரி மண்டி விலை அறிக்கை" else "Uzhav X - Daily Mandi Market Price Report"
                c.drawText(title, 20f, 32f, textPaint)

                textPaint.textSize = 10f
                textPaint.typeface = Typeface.DEFAULT
                val subtitle = "Tamil Nadu Farmers Agriculture Hub | Date: ${getReadableDate()}"
                c.drawText(subtitle, 20f, 50f, textPaint)

                // Footer
                paint.color = Color.parseColor("#EEEEEE")
                c.drawRect(0f, pageHeight - 30f, pageWidth.toFloat(), pageHeight.toFloat(), paint)

                textPaint.color = Color.DKGRAY
                textPaint.textSize = 9f
                val footerText = "Page $pageNumber | Generated for Tamil Nadu Farmers via Uzhav X"
                c.drawText(footerText, 20f, pageHeight - 12f, textPaint)
            }

            drawHeader(canvas)
            startY = 85f

            // Table Header Bar
            paint.color = Color.parseColor("#2E7D32")
            canvas.drawRect(20f, startY, pageWidth - 20f, startY + 24f, paint)

            textPaint.color = Color.WHITE
            textPaint.textSize = 10f
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

            canvas.drawText("Crop / பயிர்", 28f, startY + 16f, textPaint)
            canvas.drawText("Market / மண்டி", 185f, startY + 16f, textPaint)
            canvas.drawText("Min (₹)", 320f, startY + 16f, textPaint)
            canvas.drawText("Max (₹)", 375f, startY + 16f, textPaint)
            canvas.drawText("Modal Price", 435f, startY + 16f, textPaint)
            canvas.drawText("Unit", 515f, startY + 16f, textPaint)

            startY += 24f

            val rowHeight = 28f
            paint.style = Paint.Style.FILL

            for ((index, item) in prices.withIndex()) {
                if (startY + rowHeight > pageHeight - 45f) {
                    pdfDoc.finishPage(page)
                    pageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                    page = pdfDoc.startPage(pageInfo)
                    canvas = page.canvas
                    drawHeader(canvas)
                    startY = 80f

                    // Repeat table header
                    paint.color = Color.parseColor("#2E7D32")
                    canvas.drawRect(20f, startY, pageWidth - 20f, startY + 24f, paint)
                    textPaint.color = Color.WHITE
                    textPaint.textSize = 10f
                    textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    canvas.drawText("Crop / பயிர்", 28f, startY + 16f, textPaint)
                    canvas.drawText("Market / மண்டி", 185f, startY + 16f, textPaint)
                    canvas.drawText("Min (₹)", 320f, startY + 16f, textPaint)
                    canvas.drawText("Max (₹)", 375f, startY + 16f, textPaint)
                    canvas.drawText("Modal Price", 435f, startY + 16f, textPaint)
                    canvas.drawText("Unit", 515f, startY + 16f, textPaint)
                    startY += 24f
                }

                // Alternate row background
                if (index % 2 == 0) {
                    paint.color = Color.parseColor("#F5F5F5")
                } else {
                    paint.color = Color.WHITE
                }
                canvas.drawRect(20f, startY, pageWidth - 20f, startY + rowHeight, paint)

                textPaint.color = Color.BLACK
                textPaint.textSize = 9f
                textPaint.typeface = Typeface.DEFAULT

                val cropLabel = if (language == AppLanguage.TAMIL && item.tamilName.isNotBlank()) item.tamilName else item.cropName
                val cropDisplay = if (cropLabel.length > 24) cropLabel.substring(0, 22) + ".." else cropLabel
                val marketDisplay = if (item.marketName.length > 20) item.marketName.substring(0, 18) + ".." else item.marketName

                canvas.drawText(cropDisplay, 28f, startY + 18f, textPaint)
                canvas.drawText(marketDisplay, 185f, startY + 18f, textPaint)
                canvas.drawText("₹${item.minPrice}", 320f, startY + 18f, textPaint)
                canvas.drawText("₹${item.maxPrice}", 375f, startY + 18f, textPaint)

                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textPaint.color = Color.parseColor("#1B5E20")
                canvas.drawText("₹${item.modalPrice}", 435f, startY + 18f, textPaint)

                textPaint.typeface = Typeface.DEFAULT
                textPaint.color = Color.DKGRAY
                val unitShort = item.unit.substringBefore(" (").take(10)
                canvas.drawText(unitShort, 515f, startY + 18f, textPaint)

                startY += rowHeight
            }

            pdfDoc.finishPage(page)

            val fileName = "Mandi_Prices_${getTimestamp()}.pdf"
            val file = File(getExportDirectory(context), fileName)
            FileOutputStream(file).use { out ->
                pdfDoc.writeTo(out)
            }
            pdfDoc.close()

            Log.d(TAG, "Mandi prices PDF created: ${file.absolutePath}")
            file
        } catch (e: Exception) {
            Log.e(TAG, "PDF Export failed: ${e.message}", e)
            null
        }
    }

    // =========================================================================
    // 4. PDF EXPORT FOR GOVERNMENT SUBSIDIES
    // =========================================================================
    fun exportSubsidiesPdf(context: Context, schemes: List<GovtScheme>, language: AppLanguage): File? {
        return try {
            val pdfDoc = PdfDocument()
            val pageWidth = 595
            val pageHeight = 842
            var pageNumber = 1

            var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            var page = pdfDoc.startPage(pageInfo)
            var canvas = page.canvas

            val paint = Paint()
            val textPaint = Paint().apply { isAntiAlias = true }

            fun drawHeader(c: Canvas) {
                paint.color = Color.parseColor("#0D47A1") // Deep Blue
                c.drawRect(0f, 0f, pageWidth.toFloat(), 65f, paint)

                textPaint.color = Color.WHITE
                textPaint.textSize = 17f
                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                val title = if (language == AppLanguage.TAMIL) "உழவன் எக்ஸ் - அரசு வேளாண் மானிய திட்டங்கள் 2026" else "Uzhav X - Govt Agriculture Subsidies Guide 2026"
                c.drawText(title, 20f, 32f, textPaint)

                textPaint.textSize = 10f
                textPaint.typeface = Typeface.DEFAULT
                val subtitle = "Government of Tamil Nadu & PM-Kisan Schemes | Date: ${getReadableDate()}"
                c.drawText(subtitle, 20f, 50f, textPaint)

                paint.color = Color.parseColor("#EEEEEE")
                c.drawRect(0f, pageHeight - 30f, pageWidth.toFloat(), pageHeight.toFloat(), paint)

                textPaint.color = Color.DKGRAY
                textPaint.textSize = 9f
                c.drawText("Page $pageNumber | Saved Subsidies Directory", 20f, pageHeight - 12f, textPaint)
            }

            drawHeader(canvas)
            var startY = 80f

            for (scheme in schemes) {
                val boxHeight = 105f
                if (startY + boxHeight > pageHeight - 40f) {
                    pdfDoc.finishPage(page)
                    pageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                    page = pdfDoc.startPage(pageInfo)
                    canvas = page.canvas
                    drawHeader(canvas)
                    startY = 80f
                }

                // Card background
                paint.color = Color.parseColor("#F0F4F8")
                val rect = RectF(20f, startY, pageWidth - 20f, startY + boxHeight)
                canvas.drawRoundRect(rect, 8f, 8f, paint)

                // Left color accent stripe
                paint.color = Color.parseColor("#1565C0")
                canvas.drawRoundRect(RectF(20f, startY, 26f, startY + boxHeight), 4f, 4f, paint)

                textPaint.color = Color.parseColor("#0D47A1")
                textPaint.textSize = 12f
                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

                val schemeTitle = if (language == AppLanguage.TAMIL && scheme.tamilTitle.isNotBlank()) scheme.tamilTitle else scheme.title
                canvas.drawText(schemeTitle.take(55), 35f, startY + 22f, textPaint)

                textPaint.color = Color.parseColor("#2E7D32")
                textPaint.textSize = 10f
                textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText("Grant/Subsidy: ${scheme.maxFinancialBenefit} (${scheme.subsidyPercentage})", 35f, startY + 40f, textPaint)

                textPaint.color = Color.DKGRAY
                textPaint.textSize = 9.5f
                textPaint.typeface = Typeface.DEFAULT
                canvas.drawText("Dept: ${scheme.department}  |  Code: ${scheme.schemeCode}", 35f, startY + 56f, textPaint)

                val summaryText = if (language == AppLanguage.TAMIL && scheme.summaryTamil.isNotBlank()) scheme.summaryTamil else scheme.summaryEnglish
                canvas.drawText("Summary: ${summaryText.take(85)}...", 35f, startY + 72f, textPaint)

                textPaint.color = Color.parseColor("#1565C0")
                canvas.drawText("Portal: ${scheme.officialPortalUrl}", 35f, startY + 88f, textPaint)

                startY += boxHeight + 12f
            }

            pdfDoc.finishPage(page)

            val fileName = "Govt_Subsidies_${getTimestamp()}.pdf"
            val file = File(getExportDirectory(context), fileName)
            FileOutputStream(file).use { out ->
                pdfDoc.writeTo(out)
            }
            pdfDoc.close()

            Log.d(TAG, "Subsidies PDF created: ${file.absolutePath}")
            file
        } catch (e: Exception) {
            Log.e(TAG, "PDF Export failed: ${e.message}", e)
            null
        }
    }

    // =========================================================================
    // 5. SHARE / OPEN FILE VIA INTENT
    // =========================================================================
    fun openOrShareFile(context: Context, file: File, mimeType: String, chooserTitle: String) {
        try {
            val authority = "${context.packageName}.fileprovider"
            val contentUri: Uri = FileProvider.getUriForFile(context, authority, file)

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_SUBJECT, file.name)
                putExtra(Intent.EXTRA_TEXT, "Exported from Uzhav X App - ${file.name}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(intent, chooserTitle).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to share/open file: ${e.message}", e)
            Toast.makeText(context, "Saved to ${file.name}", Toast.LENGTH_LONG).show()
        }
    }
}
