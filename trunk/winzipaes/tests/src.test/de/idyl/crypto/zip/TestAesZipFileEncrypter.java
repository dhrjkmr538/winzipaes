package de.idyl.crypto.zip;

import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.Deflater;
import java.util.zip.ZipOutputStream;

import org.junit.Test;

import de.idyl.crypto.zip.impl.ExtZipEntry;


public class TestAesZipFileEncrypter extends TestAesZipBase {

	@Test
	public void testVariousFileTypes() throws Exception {
		String zipFileName = "tmpZipFile.zip";
		File zipFile = getOutFile(zipFileName);
		AesZipFileEncrypter enc = new AesZipFileEncrypter(zipFile);
		enc.add(getInFile("jpgSmall.jpg"), PASSWORD);
		enc.add(getInFile("textMedium.txt"), PASSWORD);
		enc.add(getInFile("textLong.txt"), PASSWORD);
		enc.close();
		
		AesZipFileDecrypter dec = new AesZipFileDecrypter(zipFile);
		File outFile = getOutFile("jpgSmall.jpg");
		dec.extractEntry(dec.getEntry("jpgSmall.jpg"), outFile, PASSWORD);
		outFile = getOutFile("textMedium.txt");
		dec.extractEntry(dec.getEntry("textMedium.txt"), outFile, PASSWORD);
		outFile = getOutFile("textLong.txt");
		dec.extractEntry(dec.getEntry("textLong.txt"), outFile, PASSWORD);
		dec.close();
	}
	
	@Test
	public void testMultipleFilesInZipFile() throws Exception {
		String fileName1 = "file1.txt";
		String fileContent1 = "file1file1file1file1file1";
		String fileName2 = "file2.txt";
		String fileContent2 = "file2file2file2file2file2file2file2file2file2";
		String fileName3 = "test\\out\\foo\\file3.txt";
		String fileContent3 = "file3file3file3file3file3file3file3file3file3file3file3file3file3file3file3file3file3";

		File tmpZipFile = getOutFile("tmpFile.zip");
		ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(tmpZipFile));
		zout.setLevel(Deflater.BEST_COMPRESSION);
		addZipEntry(fileName1, fileContent1, zout);
		addZipEntry(fileName2, fileContent2, zout);
		addZipEntry(fileName3, fileContent3, zout);
		zout.close();

		String password = "123456";
		File aesFile = getOutFile("aesFile.zip");
		AesZipFileEncrypter aesEncryptor = new AesZipFileEncrypter(aesFile);
		aesEncryptor.addAll(tmpZipFile, password);
		aesEncryptor.close();
		tmpZipFile.delete();

		AesZipFileDecrypter aesDecrypter = new AesZipFileDecrypter(aesFile);
		
		checkZipEntry( aesDecrypter, fileName1, fileContent1, password );
		checkZipEntry( aesDecrypter, fileName2, fileContent2, password );
		checkZipEntry( aesDecrypter, fileName3, fileContent3, password );
		
		ExtZipEntry entry = aesDecrypter.getEntry(fileName3);
		File extractedFile = new File(entry.getName()); 
		aesDecrypter.extractEntry( entry, extractedFile, password);
		aesDecrypter.close();
	}
	
}
