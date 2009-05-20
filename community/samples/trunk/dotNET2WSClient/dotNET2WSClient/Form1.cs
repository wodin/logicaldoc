using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.IO;
using System.Web;
using System.Net;
using System.Collections.Specialized;

using LogicalDOCWSClient.Logicaldoc;

namespace LogicalDOCWSClient
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private void GetDocumentInfo(object sender, EventArgs e)
        {
            //WARNING: FOR .NET 2.0 (VS 2005) MAKE SURE that the the mtom switch (flag mtom-enabled) is turned off in WEB-INF\.plugins\logicaldoc-webservice@3.6.0\classes\com\logicaldoc\webservice\context.xml 
        //<jaxws:properties>
        //    <entry key="mtom-enabled" value="false" />
        //</jaxws:properties>

            long documentId = long.Parse(documentIdField.Text);
            DmsService dms = new DmsService();
            documentInfo di = dms.downloadDocumentInfo("admin", "admin", documentId);
            Console.WriteLine(di.title);
            Console.WriteLine(di.sourceDate);

            textBox2.Text = di.title;
            textBox2.Text += "\r\n" + di.sourceDate;
        }

        private void Search(object sender, EventArgs e)
        {
            try
            {
                DmsService dms = new DmsService();
                string query = queryField.Text;
                searchResult sr = dms.search("admin", "admin", query, "", "en", 20, null, null);
                Console.WriteLine("HITS: " +sr.totalHits);
                Console.WriteLine("search completed in ms: " + sr.time);
                textBox2.Text = "HITS: " + sr.totalHits;
                textBox2.Text += "\r\nsearch completed in ms: " + sr.time;

                result[] results = sr.result;
                if (results != null)
                {
                    foreach (result res in results)
                    {
                        Console.WriteLine("title: " + res.title);
                        Console.WriteLine("res.id: " + res.id);                        
                        Console.WriteLine("res.summary: " + res.summary);
                        Console.WriteLine("res.length: " + res.length);
                        Console.WriteLine("res.date: " + res.date);
                        Console.WriteLine("res.type: " + res.type);
                        Console.WriteLine("res.score: " + res.score);

                        textBox2.Text += "\r\n title: " + res.title;
                        textBox2.Text += "\r\n res.id: " + res.id;
                        textBox2.Text += "\r\n res.summary: " + res.summary;
                        textBox2.Text += "\r\n res.length: " + res.length;
                        textBox2.Text += "\r\n res.date: " + res.date;
                        textBox2.Text += "\r\n res.type: " + res.type;
                        textBox2.Text += "\r\n res.score: " + res.score;

                        // Download a document
                        DownloadDocument(res.id);
                    }
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine("ex.Message = " + ex.Message);
                Console.WriteLine("ex.StackTrace = " + ex.StackTrace);
                Console.WriteLine("ex.InnerException = " + ex.InnerException);
                Console.WriteLine("ex.Source = " + ex.Source);
            }
        }


        private void DownloadDocument(long documentId)
        {
            DmsService dms = new DmsService();
            //Obtain document's metadata
            documentInfo di = dms.downloadDocumentInfo("admin", "admin", documentId);
            Console.WriteLine("di = " + di);
            textBox2.Text += "\r\ndi = " + di;

            string downloadFolder = downloadFolderField.Text;
            FileStream outStream = File.Create(downloadFolder + "/" + di.filename);
            Console.WriteLine("Created file: " + outStream.Name);
            textBox2.Text += "\r\nCreated file: \r\n" + outStream.Name;

            byte[] content = dms.downloadDocument("admin", "admin", documentId, "1.0");
            Console.WriteLine("document downloaded.");
            textBox2.Text += "\r\n\r\ndocument downloaded.";

            //// use a BinaryWriter to write formatted data to the file
            BinaryWriter bw = new BinaryWriter(outStream);

            bw.Write(content);

            // flush and close
            bw.Flush();
            bw.Close();

            Console.WriteLine("Download of documentId: " + documentId + " completed.");
            textBox2.Text += "\r\nDownload of documentId: " + documentId + " completed.";
        }

        private void button3_Click(object sender, EventArgs e)
        {
            long documentId = long.Parse(documentIdField.Text);
            DownloadDocument(documentId);
        }

        private void button4_Click(object sender, EventArgs e)
        {
            // Creates a new documrent
            DmsService dms = new DmsService();

            string fileToBeUploaded = uploadFileField.Text;
            FileInfo fi = new FileInfo(fileToBeUploaded);
            byte[] content = File.ReadAllBytes(fi.FullName);


            // This will create a new document inside the folder 5 (Document - Root)
            // The result is the Id of the new created document, otherwise error...
            string result = dms.createDocument("admin", "admin", 5, "docTitle", "dotNET2WSClient", "2009-04-05", "author",
                    "sourceType", "coverage", "en", "keywords", "versionDesc", fi.Name, content, "", null, "sourceId", "object", "recipient");
            
            this.documentIdField.Text = result;

            Console.WriteLine("Created new document: " + result);
            textBox2.Text = "Created new document: " + result;
        }


        private string SelectTextFile(string initialDirectory)
        {
            OpenFileDialog dialog = new OpenFileDialog();
            dialog.Filter = "txt files (*.txt)|*.txt|All files (*.*)|*.*";
            dialog.InitialDirectory = initialDirectory;
            dialog.Title = "Select a text file";
            return (dialog.ShowDialog() == DialogResult.OK) ? dialog.FileName : null;
        }

        private void button5_Click(object sender, EventArgs e)
        {
            string filePath = SelectTextFile("C:\tmp");
            Console.WriteLine(filePath);
            textBox2.Text = "Selected file: \r\n" + filePath;
            uploadFileField.Text = filePath;
        }

    }

}