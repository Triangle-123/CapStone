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
   $pw = $_POST['u_pw']; 
   $name = $_POST['u_name'];
   $bd = $_POST['u_bd'];
   $gd = $_POST['u_gd'];
   $tel = $_POST['u_tel'];
 
   $sql = "INSERT INTO USER(userID, userPassword, userName, userBirthdate, userGender, userPhone) VALUES('$id', '$pw', '$name', '$bd', '$gd', '$tel')";
 
   $result = mysqli_query($connect, $sql);
 
   if(!$result)
            die("mysql query error");
   else
        echo "insert success"
 
?>