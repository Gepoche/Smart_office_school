<?php
$con = mysqli_connect('localhost', 'root', '1234', 'test_one');

if($_SERVER['REQUEST_METHOD']=='POST') {
	$uNo = $_GET['uNo'];
	$uId = $_GET['uId'];
	$uPw = $_GET['uPw'];
	$lastCheck = $_GET['lastCheck'];

	$query = "update user set uId='$uId', uPw='$uPw', lastCheck='$lastCheck' where uNo=$uNo";
	if(mysqli_query($con, $query)) {
		echo "update for uNo $uNo successful";
	} else {
		echo "failed";
	}
} else {
	$query = 'select * from user';

	if($result = mysqli_query($con, $query)) {
		$rowNum = mysqli_num_rows($result);
		echo '{';

		echo "\"today\":\""; echo date('Y-m-d', time()); echo "\",";

		echo "\"rownum\":\"$rowNum\",";
		echo "\"result\":";

		echo "[";
		for($i = 0; $i < $rowNum; $i++) {
			$row = mysqli_fetch_array($result);
			echo "{";
			echo "\"uNo\":\"$row[uNo]\", \"uId\":\"$row[uId]\", \"uPw\":\"$row[uPw]\", \"uName\":\"$row[uName]\", \"lastCheck\":\"$row[lastCheck]\"";
			echo "}";
			if($i < $rowNum-1) {
				echo ",";
			}
		}
		echo "]}";
	} else {
		echo "Database not available";
	}
}
?>


