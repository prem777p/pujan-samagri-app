package com.prem.pujansamagri.service

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class PDFDocument(val context: Context) {

    @Throws(IOException::class)
    fun generateMultiPagePdf( contentList: ArrayList<String>, header: String, footer: String, filename: String): File? {

        // Create a PdfDocument instance
        val pdfDocument = PdfDocument()
        val paint = Paint()
        val titlePaint = Paint()
        val footerPaint = Paint()
        val borderPaint = Paint()

        // Page dimensions (A4 size)
        val pageWidth = 595
        val pageHeight = 842

        val margin = 60 // Page margin
        val lineSpacing = 23 // Spacing between lines

        // Header styling
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
        titlePaint.textSize = 30f
        titlePaint.textAlign = Paint.Align.CENTER

        // Footer styling
        footerPaint.textSize = 15f
        footerPaint.textAlign = Paint.Align.LEFT

        // Content styling
        paint.textSize = 19f
        paint.typeface = Typeface.DEFAULT

        // Border styling
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = 3f
        borderPaint.color = Color.BLACK

        val linesPerPage =
            (pageHeight - (margin * 2 + 80)) / lineSpacing // Calculate max lines per page
        val totalLines = contentList.size
        var currentLineIndex = 0

        // Loop to create pages
        var pageIndex = 1
        while (currentLineIndex < totalLines) {
            // Create a new page
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageIndex).create()
            val page = pdfDocument.startPage(pageInfo)

            val canvas = page.canvas

            // Draw the border
            val borderMargin = 15f // Border margin
            canvas.drawRect(
                borderMargin,
                borderMargin,
                pageWidth - borderMargin,
                pageHeight - borderMargin,
                borderPaint
            )

            // Draw the header
            canvas.drawText(header, (pageWidth / 2).toFloat(), margin.toFloat(), titlePaint)

            // Draw the content
            val x = margin
            var y = margin + 40 // Starting Y position after header
            var i = 0
            while (i < linesPerPage && currentLineIndex < totalLines) {
                val line = contentList[currentLineIndex]
                line.lines().forEach { l ->
                    canvas.drawText(l.trim(), x.toFloat(), y.toFloat(), paint)
                    y += lineSpacing
                    i++
                }
                currentLineIndex++
            }

            // Draw the footer
            canvas.drawText(
                footer,
                (pageWidth / 1.7).toFloat(),
                (pageHeight - margin + 20).toFloat(),
                footerPaint
            )

            // Finish the current page
            pdfDocument.finishPage(page)
            pageIndex++
        }

        // Define file path
        val directory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath,
            "Pujan Samagri"
        )
        if (!directory.exists()) {
            directory.mkdirs()
        }
        // Create a temporary file
        if (directory.list()?.contains("$filename.pdf") == true) {
            return null
        }
        val pdfFile = File(directory, "$filename.pdf")

        try {
            FileOutputStream(pdfFile).use { fos ->
                pdfDocument.writeTo(fos)
            }
        } finally {
            pdfDocument.close()
        }

        // Return the temporary file
        return pdfFile
    }


}