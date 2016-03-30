namespace ControlU2FKeys
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(Form1));
            this.tbKeyHandle = new System.Windows.Forms.TextBox();
            this.label1 = new System.Windows.Forms.Label();
            this.btRegister = new System.Windows.Forms.Button();
            this.btDelete = new System.Windows.Forms.Button();
            this.tbHost = new System.Windows.Forms.TextBox();
            this.tbPort = new System.Windows.Forms.TextBox();
            this.tbUrl = new System.Windows.Forms.TextBox();
            this.label3 = new System.Windows.Forms.Label();
            this.label5 = new System.Windows.Forms.Label();
            this.statusStrip1 = new System.Windows.Forms.StatusStrip();
            this.toolStripStatusLabel1 = new System.Windows.Forms.ToolStripStatusLabel();
            this.btSave = new System.Windows.Forms.Button();
            this.label7 = new System.Windows.Forms.Label();
            this.cbUser = new System.Windows.Forms.ComboBox();
            this.tbPublicKey = new System.Windows.Forms.TextBox();
            this.label2 = new System.Windows.Forms.Label();
            this.statusStrip1.SuspendLayout();
            this.SuspendLayout();
            // 
            // tbKeyHandle
            // 
            this.tbKeyHandle.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.tbKeyHandle.Location = new System.Drawing.Point(111, 119);
            this.tbKeyHandle.Multiline = true;
            this.tbKeyHandle.Name = "tbKeyHandle";
            this.tbKeyHandle.ReadOnly = true;
            this.tbKeyHandle.Size = new System.Drawing.Size(224, 60);
            this.tbKeyHandle.TabIndex = 9;
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(12, 122);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(59, 13);
            this.label1.TabIndex = 8;
            this.label1.Text = "KeyHandle";
            // 
            // btRegister
            // 
            this.btRegister.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.btRegister.Location = new System.Drawing.Point(260, 291);
            this.btRegister.Name = "btRegister";
            this.btRegister.Size = new System.Drawing.Size(75, 23);
            this.btRegister.TabIndex = 13;
            this.btRegister.Text = "Register Key";
            this.btRegister.UseVisualStyleBackColor = true;
            this.btRegister.Click += new System.EventHandler(this.btRegister_Click);
            // 
            // btDelete
            // 
            this.btDelete.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.btDelete.Location = new System.Drawing.Point(111, 291);
            this.btDelete.Name = "btDelete";
            this.btDelete.Size = new System.Drawing.Size(75, 23);
            this.btDelete.TabIndex = 12;
            this.btDelete.Text = "Delete Key";
            this.btDelete.UseVisualStyleBackColor = true;
            this.btDelete.Click += new System.EventHandler(this.btDelete_Click);
            // 
            // tbHost
            // 
            this.tbHost.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.tbHost.Location = new System.Drawing.Point(111, 38);
            this.tbHost.Name = "tbHost";
            this.tbHost.Size = new System.Drawing.Size(143, 20);
            this.tbHost.TabIndex = 3;
            this.tbHost.TextAlign = System.Windows.Forms.HorizontalAlignment.Right;
            // 
            // tbPort
            // 
            this.tbPort.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.tbPort.Location = new System.Drawing.Point(260, 38);
            this.tbPort.Name = "tbPort";
            this.tbPort.Size = new System.Drawing.Size(75, 20);
            this.tbPort.TabIndex = 4;
            // 
            // tbUrl
            // 
            this.tbUrl.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.tbUrl.Location = new System.Drawing.Point(111, 64);
            this.tbUrl.Name = "tbUrl";
            this.tbUrl.Size = new System.Drawing.Size(224, 20);
            this.tbUrl.TabIndex = 6;
            this.tbUrl.TextAlign = System.Windows.Forms.HorizontalAlignment.Right;
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(12, 41);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(59, 13);
            this.label3.TabIndex = 2;
            this.label3.Text = "Host / Port";
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Location = new System.Drawing.Point(12, 67);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(29, 13);
            this.label5.TabIndex = 5;
            this.label5.Text = "URL";
            // 
            // statusStrip1
            // 
            this.statusStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripStatusLabel1});
            this.statusStrip1.Location = new System.Drawing.Point(0, 324);
            this.statusStrip1.Name = "statusStrip1";
            this.statusStrip1.Size = new System.Drawing.Size(347, 22);
            this.statusStrip1.TabIndex = 14;
            // 
            // toolStripStatusLabel1
            // 
            this.toolStripStatusLabel1.Name = "toolStripStatusLabel1";
            this.toolStripStatusLabel1.Size = new System.Drawing.Size(0, 17);
            // 
            // btSave
            // 
            this.btSave.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.btSave.Location = new System.Drawing.Point(260, 90);
            this.btSave.Name = "btSave";
            this.btSave.Size = new System.Drawing.Size(75, 23);
            this.btSave.TabIndex = 7;
            this.btSave.Text = "Save values";
            this.btSave.UseVisualStyleBackColor = true;
            this.btSave.Click += new System.EventHandler(this.btSave_Click);
            // 
            // label7
            // 
            this.label7.AutoSize = true;
            this.label7.Location = new System.Drawing.Point(12, 14);
            this.label7.Name = "label7";
            this.label7.Size = new System.Drawing.Size(29, 13);
            this.label7.TabIndex = 0;
            this.label7.Text = "User";
            // 
            // cbUser
            // 
            this.cbUser.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.cbUser.FormattingEnabled = true;
            this.cbUser.Location = new System.Drawing.Point(111, 11);
            this.cbUser.Name = "cbUser";
            this.cbUser.Size = new System.Drawing.Size(224, 21);
            this.cbUser.TabIndex = 1;
            this.cbUser.SelectedIndexChanged += new System.EventHandler(this.cbUser_SelectedIndexChanged);
            // 
            // tbPublicKey
            // 
            this.tbPublicKey.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.tbPublicKey.Location = new System.Drawing.Point(111, 185);
            this.tbPublicKey.Multiline = true;
            this.tbPublicKey.Name = "tbPublicKey";
            this.tbPublicKey.ReadOnly = true;
            this.tbPublicKey.Size = new System.Drawing.Size(224, 100);
            this.tbPublicKey.TabIndex = 11;
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(9, 188);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(57, 13);
            this.label2.TabIndex = 10;
            this.label2.Text = "Public Key";
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(347, 346);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.tbPublicKey);
            this.Controls.Add(this.cbUser);
            this.Controls.Add(this.label7);
            this.Controls.Add(this.btSave);
            this.Controls.Add(this.statusStrip1);
            this.Controls.Add(this.label5);
            this.Controls.Add(this.label3);
            this.Controls.Add(this.tbUrl);
            this.Controls.Add(this.tbPort);
            this.Controls.Add(this.tbHost);
            this.Controls.Add(this.btDelete);
            this.Controls.Add(this.btRegister);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.tbKeyHandle);
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.Name = "Form1";
            this.Text = "CrySIL U2F Key Manager";
            this.Load += new System.EventHandler(this.Form1_Load);
            this.statusStrip1.ResumeLayout(false);
            this.statusStrip1.PerformLayout();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.TextBox tbKeyHandle;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Button btRegister;
        private System.Windows.Forms.Button btDelete;
        private System.Windows.Forms.TextBox tbHost;
        private System.Windows.Forms.TextBox tbPort;
        private System.Windows.Forms.TextBox tbUrl;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.StatusStrip statusStrip1;
        private System.Windows.Forms.Button btSave;
        private System.Windows.Forms.ToolStripStatusLabel toolStripStatusLabel1;
        private System.Windows.Forms.Label label7;
        private System.Windows.Forms.ComboBox cbUser;
        private System.Windows.Forms.TextBox tbPublicKey;
        private System.Windows.Forms.Label label2;
    }
}

