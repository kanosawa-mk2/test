
CREATE TABLE [dbo].[TestTable](
	[ID] [int] NOT NULL PRIMARY KEY,
	[Name] [nvarchar](255) NULL,
	[Age] [int] NULL,
	[Email] [nvarchar](255) NULL,
);





CREATE TABLE test_table (
   ID INT NOT NULL IDENTITY(1,1) PRIMARY KEY,
   Name VARCHAR(50) NULL,
   Money decimal(5,5) null,
   Birthday DATE NULL,
   InsDate datetime NULL,
   Img Image NULL,
);
