<?php
$conn = mysqli_connect("localhost", "root", "1234", "test_one");
$query = "select * from user_data";
if($result = mysqli_query($conn, $query)){
    $rowNum = mysqli_num_rows($result);
    echo "{";
    echo "\"status\":\"OK\",";
    echo "\"rownum\":\"$rowNum\",";
    echo "\"result\":";
    echo "[";
    for($i = 0; $i < $rowNum; $i++){
        $row = mysqli_fetch_array($result);
        echo "{";
        echo "\"name\":\"$row[name]\", \"ident\":\"$row[ident]\", \"password\":\"$row[password]\"";
        echo "}";
        if($i<$rowNum-1){
            echo ",";
        }
    }
    echo "]";
    echo "}";
} else{
    echo "Database not available";
}
?>
