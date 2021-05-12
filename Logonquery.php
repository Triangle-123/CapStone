<?php
    header('content-type: text/html; charset=utf-8'); 
    $con=mysqli_connect( "localhost", "hong123", "qrad9768!") or  
    die( "SQL server에 연결할 수 없습니다.");
 
    mysqli_query($con, "SET NAMES UTF8");
    // 데이터베이스 선택
    mysqli_select_db($con, "hong123");

    // 세션 시작
    session_start();

    if (mysqli_connect_errno($con))
    {
        echo "Failed to connect to MySQL: " . mysqli_connect_error();
    }
 
    $ID = $_GET['ID'];
    $sql = "SELECT userName FROM USER where userID ='$ID'";

    $result = mysqli_query($con, $sql);
 
    $row = mysqli_fetch_array($result);
    $data = $row[0];
 
    if($data){
        echo $data;
    }
    mysqli_close($con);
?>