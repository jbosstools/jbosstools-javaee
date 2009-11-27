package org.jboss.tools.jsf.vpe.seam.test;

import org.jboss.tools.vpe.ui.test.ComponentContentTest;

public class SeamComponentContentTest extends ComponentContentTest {

	public SeamComponentContentTest(String name) {
		super(name);
		setCheckWarning(false);
	}

	@Override
	protected String getTestProjectName() {
		return SeamAllTests.IMPORT_PROJECT_NAME;
	}

	public void testButton() throws Throwable {
		performContentTest("components/button.xhtml"); //$NON-NLS-1$
	}

	public void testCache() throws Throwable {
		performContentTest("components/cache.xhtml"); //$NON-NLS-1$
	}

	public void testConversationId() throws Throwable {
		performContentTest("components/conversationId.xhtml"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testConvertDateTime() throws Throwable {
		performInvisibleTagTest("components/convertDateTime.xhtml", "id1"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testConvertEntity() throws Throwable {
		performInvisibleTagTest("components/convertEntity.xhtml", "id1"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testConvertEnum() throws Throwable {
		performInvisibleTagTest("components/convertEnum.xhtml", "id1"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testDecorate() throws Throwable {
		performContentTest("components/decorate.xhtml"); //$NON-NLS-1$
	}

	public void testDefaultAction() throws Throwable {
		performInvisibleTagTest("components/defaultAction.xhtml", "id1"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testDiv() throws Throwable {
		performContentTest("components/div.xhtml"); //$NON-NLS-1$
	}

	public void testEnumItem() throws Throwable {
		performContentTest("components/enumItem.xhtml"); //$NON-NLS-1$
	}

	public void testFileUpload() throws Throwable {
		performContentTest("components/fileUpload.xhtml"); //$NON-NLS-1$
	}

	public void testFormattedText() throws Throwable {
		performContentTest("components/formattedText.xhtml"); //$NON-NLS-1$
	}

	public void testFragment() throws Throwable {
		performContentTest("components/fragment.xhtml"); //$NON-NLS-1$
	}

	public void testGraphicImage() throws Throwable {
		performContentTest("components/graphicImage.xhtml"); //$NON-NLS-1$
	}

	public void testLabel() throws Throwable {
		performContentTest("components/label.xhtml"); //$NON-NLS-1$
	}

	public void testLink() throws Throwable {
		performContentTest("components/link.xhtml"); //$NON-NLS-1$
	}

	public void testMessage() throws Throwable {
		performContentTest("components/message.xhtml"); //$NON-NLS-1$
	}

	public void testRemote() throws Throwable {
		performContentTest("components/remote.xhtml"); //$NON-NLS-1$
	}

	public void testSelectDate() throws Throwable {
		performContentTest("components/selectDate.xhtml"); //$NON-NLS-1$
	}

	public void testSelectItems() throws Throwable {
		performContentTest("components/selectItems.xhtml"); //$NON-NLS-1$
	}

	public void testSpan() throws Throwable {
		performContentTest("components/span.xhtml"); //$NON-NLS-1$
	}

	public void testTransformImageSize() throws Throwable {
		performContentTest("components/transformImageSize.xhtml"); //$NON-NLS-1$
	}

	public void testTransformImageBlur() throws Throwable {
		performInvisibleTagTest("components/transformImageBlur.xhtml", "id1"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testTransformImageType() throws Throwable {
		performInvisibleTagTest("components/transformImageType.xhtml", "id1"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testValidate() throws Throwable {
		performInvisibleTagTest("components/validate.xhtml", "id1"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testValidateAll() throws Throwable {
		performContentTest("components/validateAll.xhtml"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testValidateFormattedText() throws Throwable {
		performInvisibleTagTest("components/validateFormattedText.xhtml", "id1"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testTaskId() throws Throwable {
		performInvisibleTagTest("components/taskId.xhtml", "id1"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	// mail components

	public void testMailMessage() throws Throwable {
		performContentTest("components/mail/message.xhtml");
	}

	public void testMailBody() throws Throwable {
		performContentTest("components/mail/body.xhtml");
	}

	public void testMailFrom() throws Throwable {
		performInvisibleTagTest("components/mail/from.xhtml", "id1");
	}

	public void testMailTo() throws Throwable {
		performInvisibleTagTest("components/mail/to.xhtml", "id1");
	}

	public void testMailSubject() throws Throwable {
		performInvisibleTagTest("components/mail/subject.xhtml", "id1");
	}

	public void testMailAttachment() throws Throwable {
		performInvisibleTagTest("components/mail/attachment.xhtml", "id1");
	}

	public void testMailBcc() throws Throwable {
		performInvisibleTagTest("components/mail/bcc.xhtml", "id1");
	}

	public void testMailCc() throws Throwable {
		performInvisibleTagTest("components/mail/cc.xhtml", "id1");
	}

	public void testMailHeader() throws Throwable {
		performInvisibleTagTest("components/mail/header.xhtml", "id1");
	}

	public void testMailReplyTo() throws Throwable {
		performInvisibleTagTest("components/mail/replyTo.xhtml", "id1");
	}

	// pdf components

	public void testPdfDocument() throws Throwable {
		performContentTest("components/pdf/document.xhtml");
	}

	public void testPdfParagraph() throws Throwable {
		performContentTest("components/pdf/paragraph.xhtml");
	}

	public void testPdfText() throws Throwable {
		performContentTest("components/pdf/text.xhtml");
	}

	public void testPdfFont() throws Throwable {
		performContentTest("components/pdf/font.xhtml");
	}

	public void testPdfNewPage() throws Throwable {
		performContentTest("components/pdf/newPage.xhtml");
	}

	public void testPdfImage() throws Throwable {
		performInvisibleTagTest("components/pdf/image.xhtml", "id1");
	}

	public void testPdfAnchor() throws Throwable {
		performContentTest("components/pdf/anchor.xhtml");
	}

	public void testPdfHeader() throws Throwable {
		performInvisibleTagTest("components/pdf/header.xhtml", "id1");
	}

	public void testPdfFooter() throws Throwable {
		performContentTest("components/pdf/footer.xhtml");
	}

	public void testPdfPageNumber() throws Throwable {
		performContentTest("components/pdf/pageNumber.xhtml");
	}

	public void testPdfChapter() throws Throwable {
		performContentTest("components/pdf/chapter.xhtml");
	}

	public void testPdfSection() throws Throwable {
		performContentTest("components/pdf/section.xhtml");
	}

	public void testPdfList() throws Throwable {
		performContentTest("components/pdf/list.xhtml");
	}

	public void testPdfListItem() throws Throwable {
		performContentTest("components/pdf/listItem.xhtml");
	}

	public void testPdfTable() throws Throwable {
		performContentTest("components/pdf/table.xhtml");
	}

	public void testPdfCell() throws Throwable {
		performContentTest("components/pdf/cell.xhtml");
	}

	public void testPdfBarChart() throws Throwable {
		performContentTest("components/pdf/barChart.xhtml");
	}

	public void testPdfLineChart() throws Throwable {
		performContentTest("components/pdf/lineChart.xhtml");
	}

	public void testPdfPieChart() throws Throwable {
		performContentTest("components/pdf/pieChart.xhtml");
	}

	public void testPdfSeries() throws Throwable {
		performInvisibleTagTest("components/pdf/series.xhtml", "id1");
	}

	public void testPdfData() throws Throwable {
		performInvisibleTagTest("components/pdf/data.xhtml", "id1");
	}

	public void testPdfColor() throws Throwable {
		performInvisibleTagTest("components/pdf/color.xhtml", "id1");
	}

	public void testPdfStroke() throws Throwable {
		performInvisibleTagTest("components/pdf/stroke.xhtml", "id1");
	}

	public void testPdfBarCode() throws Throwable {
		performInvisibleTagTest("components/pdf/barCode.xhtml", "id1");
	}

}
