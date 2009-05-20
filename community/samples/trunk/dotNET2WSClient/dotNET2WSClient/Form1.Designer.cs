namespace LogicalDOCWSClient
{
    partial class Form1
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.button1 = new System.Windows.Forms.Button();
            this.button2 = new System.Windows.Forms.Button();
            this.queryField = new System.Windows.Forms.TextBox();
            this.button3 = new System.Windows.Forms.Button();
            this.button4 = new System.Windows.Forms.Button();
            this.documentIdField = new System.Windows.Forms.TextBox();
            this.downloadFolderField = new System.Windows.Forms.TextBox();
            this.label1 = new System.Windows.Forms.Label();
            this.textBox2 = new System.Windows.Forms.TextBox();
            this.splitContainer1 = new System.Windows.Forms.SplitContainer();
            this.uploadFileField = new System.Windows.Forms.TextBox();
            this.button5 = new System.Windows.Forms.Button();
            this.splitContainer1.Panel1.SuspendLayout();
            this.splitContainer1.Panel2.SuspendLayout();
            this.splitContainer1.SuspendLayout();
            this.SuspendLayout();
            // 
            // button1
            // 
            this.button1.Location = new System.Drawing.Point(8, 87);
            this.button1.Name = "button1";
            this.button1.Size = new System.Drawing.Size(121, 23);
            this.button1.TabIndex = 0;
            this.button1.Text = "GetDocumentInfo";
            this.button1.UseVisualStyleBackColor = true;
            this.button1.Click += new System.EventHandler(this.GetDocumentInfo);
            // 
            // button2
            // 
            this.button2.Location = new System.Drawing.Point(8, 234);
            this.button2.Name = "button2";
            this.button2.Size = new System.Drawing.Size(76, 23);
            this.button2.TabIndex = 1;
            this.button2.Text = "Search";
            this.button2.UseVisualStyleBackColor = true;
            this.button2.Click += new System.EventHandler(this.Search);
            // 
            // queryField
            // 
            this.queryField.Location = new System.Drawing.Point(124, 236);
            this.queryField.Name = "queryField";
            this.queryField.Size = new System.Drawing.Size(100, 20);
            this.queryField.TabIndex = 2;
            this.queryField.Text = "query...";
            // 
            // button3
            // 
            this.button3.Location = new System.Drawing.Point(8, 194);
            this.button3.Name = "button3";
            this.button3.Size = new System.Drawing.Size(126, 23);
            this.button3.TabIndex = 3;
            this.button3.Text = "Download Document";
            this.button3.UseVisualStyleBackColor = true;
            this.button3.Click += new System.EventHandler(this.button3_Click);
            // 
            // button4
            // 
            this.button4.Location = new System.Drawing.Point(8, 45);
            this.button4.Name = "button4";
            this.button4.Size = new System.Drawing.Size(126, 23);
            this.button4.TabIndex = 4;
            this.button4.Text = "Upload Document";
            this.button4.UseVisualStyleBackColor = true;
            this.button4.Click += new System.EventHandler(this.button4_Click);
            // 
            // documentIdField
            // 
            this.documentIdField.Location = new System.Drawing.Point(155, 90);
            this.documentIdField.Name = "documentIdField";
            this.documentIdField.Size = new System.Drawing.Size(69, 20);
            this.documentIdField.TabIndex = 5;
            this.documentIdField.Text = "documentId";
            // 
            // downloadFolderField
            // 
            this.downloadFolderField.Location = new System.Drawing.Point(8, 159);
            this.downloadFolderField.Name = "downloadFolderField";
            this.downloadFolderField.Size = new System.Drawing.Size(100, 20);
            this.downloadFolderField.TabIndex = 6;
            this.downloadFolderField.Text = "C:\\tmp";
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(8, 143);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(90, 13);
            this.label1.TabIndex = 7;
            this.label1.Text = "Download Folder:";
            // 
            // textBox2
            // 
            this.textBox2.AcceptsReturn = true;
            this.textBox2.Location = new System.Drawing.Point(3, 3);
            this.textBox2.MaximumSize = new System.Drawing.Size(300, 300);
            this.textBox2.MinimumSize = new System.Drawing.Size(200, 200);
            this.textBox2.Multiline = true;
            this.textBox2.Name = "textBox2";
            this.textBox2.ScrollBars = System.Windows.Forms.ScrollBars.Both;
            this.textBox2.Size = new System.Drawing.Size(209, 300);
            this.textBox2.TabIndex = 8;
            // 
            // splitContainer1
            // 
            this.splitContainer1.Location = new System.Drawing.Point(4, 1);
            this.splitContainer1.MinimumSize = new System.Drawing.Size(200, 200);
            this.splitContainer1.Name = "splitContainer1";
            // 
            // splitContainer1.Panel1
            // 
            this.splitContainer1.Panel1.AutoScroll = true;
            this.splitContainer1.Panel1.AutoScrollMinSize = new System.Drawing.Size(200, 100);
            this.splitContainer1.Panel1.Controls.Add(this.button5);
            this.splitContainer1.Panel1.Controls.Add(this.uploadFileField);
            this.splitContainer1.Panel1.Controls.Add(this.button4);
            this.splitContainer1.Panel1.Controls.Add(this.label1);
            this.splitContainer1.Panel1.Controls.Add(this.downloadFolderField);
            this.splitContainer1.Panel1.Controls.Add(this.button1);
            this.splitContainer1.Panel1.Controls.Add(this.button3);
            this.splitContainer1.Panel1.Controls.Add(this.documentIdField);
            this.splitContainer1.Panel1.Controls.Add(this.button2);
            this.splitContainer1.Panel1.Controls.Add(this.queryField);
            // 
            // splitContainer1.Panel2
            // 
            this.splitContainer1.Panel2.Controls.Add(this.textBox2);
            this.splitContainer1.Size = new System.Drawing.Size(473, 310);
            this.splitContainer1.SplitterDistance = 248;
            this.splitContainer1.TabIndex = 9;
            // 
            // uploadFileField
            // 
            this.uploadFileField.Location = new System.Drawing.Point(8, 19);
            this.uploadFileField.Name = "uploadFileField";
            this.uploadFileField.Size = new System.Drawing.Size(151, 20);
            this.uploadFileField.TabIndex = 8;
            this.uploadFileField.Text = "File path...";
            // 
            // button5
            // 
            this.button5.Location = new System.Drawing.Point(165, 19);
            this.button5.Name = "button5";
            this.button5.Size = new System.Drawing.Size(68, 23);
            this.button5.TabIndex = 9;
            this.button5.Text = "Browse...";
            this.button5.UseVisualStyleBackColor = true;
            this.button5.Click += new System.EventHandler(this.button5_Click);
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(480, 312);
            this.Controls.Add(this.splitContainer1);
            this.Name = "Form1";
            this.Text = "Form1";
            this.splitContainer1.Panel1.ResumeLayout(false);
            this.splitContainer1.Panel1.PerformLayout();
            this.splitContainer1.Panel2.ResumeLayout(false);
            this.splitContainer1.Panel2.PerformLayout();
            this.splitContainer1.ResumeLayout(false);
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Button button1;
        private System.Windows.Forms.Button button2;
        private System.Windows.Forms.TextBox queryField;
        private System.Windows.Forms.Button button3;
        private System.Windows.Forms.Button button4;
        private System.Windows.Forms.TextBox documentIdField;
        private System.Windows.Forms.TextBox downloadFolderField;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.TextBox textBox2;
        private System.Windows.Forms.SplitContainer splitContainer1;
        private System.Windows.Forms.Button button5;
        private System.Windows.Forms.TextBox uploadFileField;
    }
}

