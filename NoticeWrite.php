<?
    header('content-type: text/html; charset=utf-8'); 
    // 데이터베이스 접속 문자열. (db위치, 유저 이름, 비밀번호)
    $connect=mysqli_connect( "localhost", "hong123", "qrad9768!") or  
        die( "SQL server에 연결할 수 없습니다.");
    
    mysqli_query($connect, "SET NAMES UTF8");
   // 데이터베이스 선택
   mysqli_select_db($connect, "hong123");
 
   // 세션 시작
   session_start();
 
   $id = $_POST['u_id'];
   $name = $_POST['u_name'];
   $nt = $_POST['time'];
   $ntt = $_POST['title'];
   $nc = $_POST['content'];
 
   $sql = "INSERT INTO notice(userID, userName, nbTime, nbTitle, nbContent) VALUES('$id', '$name', '$nt', '$ntt', '$nc')";
 
   $result = mysqli_query($connect, $sql);
 
   if(!$result)
            die("mysql query error");
   else
        echo "insert success"
 
?>