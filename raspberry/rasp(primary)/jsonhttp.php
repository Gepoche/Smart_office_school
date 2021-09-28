<?php
$con = mysqli_connect('localhost', 'root', '1234', 'test_one');

if($_SERVER['REQUEST_METHOD']=='POST') {
	$target = $_GET['target'];
	$mode = $_GET['mode'];
	$uNo = $_GET['uNo'];
	$uId = $_GET['uId'];
	$uPw = $_GET['uPw'];
	$lastCheck = $_GET['lastCheck'];
	$isAvailable = $_GET['isAvailable'];
	$startTime = $_GET['startTime'];
	$endTime = $_GET['endTime'];
	$keyValue = $_GET['keyValue'];
	$led_num = $_GET['led_num'];
	$stmt = $_GET['stmt'];

	if($target == "user") {
		$query = "update user set uId='$uId', uPw='$uPw', lastCheck='$lastCheck', isAvailable='$isAvailable' where uNo=$uNo";
		if(mysqli_query($con, $query)) {
			echo "update for uNo $uNo successful";
		} else {
			echo "failed";
		}
	} else if($target == "room") {
		$query = "";
		if($mode == "ins") {
			$query = "insert into room_book values($uNo, '$startTime', '$endTime', '$keyValue')";
		} else if($mode == "del") {
			$query = "delete from room_book where startTime = '$startTime'";
		}
		if(mysqli_query($con, $query)) {
			echo "insert/delete successful";
		} else {
			echo "failed";
		}
	} else if($target == "led") {
		$query = "update led set stmt = $stmt where led_num = $led_num";
		if(mysqli_query($con, $query)) {
			echo "update successful";
		} else {
			echo "failed";
		}
	}
} else {
	$userQuery = 'select * from user';
	$roomQuery = 'select * from room_book';
	$ledQuery = 'select * from led';

	if($userResult = mysqli_query($con, $userQuery)) {
		$userNum = mysqli_num_rows($userResult);
		echo '{';

		echo "\"today\":\""; echo date('Y-m-d', time()); echo "\",";
		echo "\"now\":\""; echo date('H:i:s', time()); echo "\",";

		echo "\"usernum\":\"$userNum\",";
		echo "\"users\":";

		echo "[";
		for($i = 0; $i < $userNum; $i++) {
			$row = mysqli_fetch_array($userResult);
			echo "{";
			echo "\"uNo\":\"$row[uNo]\", \"uId\":\"$row[uId]\", \"uPw\":\"$row[uPw]\", \"uName\":\"$row[uName]\", \"lastCheck\":\"$row[lastCheck]\", \"isAdmin\":\"$row[isAdmin]\", \"isAvailable\":\"$row[isAvailable]\"";
			echo "}";
			if($i < $userNum-1) {
				echo ",";
			}
		}
		echo "],";
		if($roomResult = mysqli_query($con, $roomQuery)) {
			$roomNum = mysqli_num_rows($roomResult);
			echo "\"booknum\":\"$roomNum\",";
			echo "\"roombooks\":";
			echo "[";
			for($i = 0; $i < $roomNum; $i++) {
				$row = mysqli_fetch_array($roomResult);
				echo "{";
				echo "\"uNo\":\"$row[uNo]\", \"startTime\":\"$row[startTime]\", \"endTime\":\"$row[endTime]\", \"keyValue\":\"$row[keyValue]\"";
				echo "}";
				if($i < $roomNum-1) {
					echo ",";
				}
			}
			echo "],";
		}
		if($ledResult = mysqli_query($con, $ledQuery)) {
			echo "\"led\":";
			echo "[";
			for($i = 0; $i < 8; $i++) {
				$row = mysqli_fetch_array($ledResult);
				echo "{";
				echo "\"led_num\":\"$row[led_num]\", \"stmt\":\"$row[stmt]\"";
				echo "}";
				if($i < 7) {
					echo ",";
				}
			}
			echo "]";
		}
		echo "}";
	} else {
		echo "Database not available";
	}
}
?>


