<?php
/*Talking Pages
Free Audiobook App
Created By: Soumya Chowdhury
            Makineedi Sai Harsh
            Nagani Vrudant Gopalbhai
            Mukul Bhambari
*/
$conn = mysqli_connect("localhost","root","","audiobooklib");

/*Function to check connection*/
if(mysqli_connect_errno())
{
	printf("Connection failed: %s\n",mysqli_connect_error());
	exit();
}

$query = "SELECT * FROM audiobook ORDER BY Id DESC";
if($result = mysqli_query($conn, $query)){
	$i = 0;
	while ($row = mysqli_fetch_row($result)){
		if($i==0){
			echo "$row[0],$row[1]";
		}
		else{
			echo "/$row[0],$row[1]";
		}
		$i++;
	}
	mysqli_free_result($result);
} 
mysqli_close($conn);
?>
