package org.alfresco.share.site.document;

import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.share.util.AbstractTests;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author Aliaksei Boole
 */
public class ManageDocLibItemsTest extends AbstractTests
{
        private static Log logger = LogFactory.getLog(ManageDocLibItemsTest.class);

        @Override
        @BeforeClass(alwaysRun = true)
        public void setup() throws Exception
        {
                super.setup();
                logger.info("Start Tests in: " + testName);

        }

        @Test(groups = "DataPrepDocumentLibrary")
        public void dataPrep_Enterprise40x_8485() throws Exception
        {
                String testName = getTestName();
                String siteName = getSiteName(testName);

                // User
                String testUser = getUserNameFreeDomain(testName);
                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

                // Login
                ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
                ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(drone);
        }

        /**
         * Upload items. With special characters.
         *
         * @throws Exception
         */
        @Test(groups = "Enterprise4.2")
        public void Enterprise40x_8485() throws Exception
        {
                /** Start Test */
                testName = getTestName();

                /** Test Data Setup */
                String siteName = getSiteName(testName);
                String testUser = getUserNameFreeDomain(testName);
                String fileWithSpecialCharacter = "désirBedürfnisèil あなたの名前は何ですか¿Cuál.txt";

                // Login
                ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
                ShareUser.openSitesDocumentLibrary(drone, siteName);
                // Uploading file with special character.
                String[] fileInfo = { fileWithSpecialCharacter };
                DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo);
                assertTrue(documentLibraryPage.isContentUploadedSucessful(fileWithSpecialCharacter), "File with special characters didn't uploaded");
        }


        @Test(groups = "DataPrepDocumentLibrary")
        public void dataPrep_Enterprise40x_8731() throws Exception
        {
                String testName = getTestName();
                String siteName = getSiteName(testName);

                // User
                String testUser = getUserNameFreeDomain(testName);
                CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUser);

                // Login
                ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
                ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
                ShareUser.logout(drone);
        }

        /**
         * Navigation to folders with '%<number>' in the name
         *
         * @throws Exception
         */
        @Test(groups = "Enterprise4.2")
        public void Enterprise40x_8731() throws Exception
        {
                //Array with folderNames.
                final String[] folderNames = { "my%folderha", "my%folder", "my%folder%25156722", "my%folder%156722again", "xyz%123" };

                /** Start Test */
                testName = getTestName();

                /** Test Data Setup */
                String siteName = getSiteName(testName);
                String testUser = getUserNameFreeDomain(testName);
                // Login
                ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
                DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
                // Create set of folders with %.
                for (String folderName : folderNames)
                {
                        ShareUserSitePage.createFolder(drone, folderName, folderName);
                        documentLibraryPage.getFileDirectoryInfo(folderName);
                }
                //Upload testFile
                String fileName = getFileName(siteName + "_testFile");
                String[] fileInfo = { fileName, folderNames[0] };
                ShareUser.uploadFileInFolder(drone, fileInfo);

                //Copy testFile to folders
                for (int i = 1; i < folderNames.length; i++)
                {
                        ShareUserSitePage.copyOrMoveToFolder(drone, siteName, fileName, new String[] { folderNames[i] }, true);
                }
                documentLibraryPage.getNavigation().clickFolderUp();

                //Check all another folders
                for (int i = 1; i < folderNames.length; i++)
                {
                        documentLibraryPage.selectFolder(folderNames[i]);
                        assertTrue(documentLibraryPage.isFileVisible(fileName), String.format("File didn't copy to folder or folder broken.(%s)", folderNames[i]));
                        documentLibraryPage.getNavigation().clickFolderUp();
                }
        }

}
