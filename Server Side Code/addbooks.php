/*Talking Pages
Free Audiobook App
Created By: Soumya Chowdhury
            Makineedi Sai Harsh
            Nagani Vrudant Gopalbhai
            Mukul Bhambari
*/
<?php
/*Make connection database*/
$conn = mysqli_connect("localhost","root","","audiobooklib");

/*Function to check connection*/
if(mysqli_connect_errno())
{
	printf("Connection failed: %s\n",mysqli_connect_error());
	exit();
}

/*Scan directory for files*/
$files = glob('*.mp3');

rsort ($files);

/*Insert list of files to database IF they don't exist already*/
$i=0;
mysqli_query($conn,"TRUNCATE TABLE audiobook;");
while ($files[$i]) 
{
	$name = basename($files[$i]);
	echo $name;
	$addquery = "INSERT INTO audiobook (Id,Name) VALUES (default,'$name')";
	mysqli_query($conn,$addquery);
	$i++;
}
?>
