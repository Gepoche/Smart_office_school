# 실행하면 데이터베이스에서 led 상태를 받아 갱신

import sys
import serial
import pymysql

conn = pymysql.connect(host="localhost", port=3306, user="root", password="1234", db="test_one", charset="utf8")
cur = conn.cursor()
sql = "select * from led"

ser = serial.Serial('/dev/ttyAMA0', 115200)

cur.execute(sql)

result = cur.fetchall()

for i in range(len(result)):
  serialCommand = ""
  if result[i][1] == 1:
    serialCommand = "L" + str(i) + "_S"
  else:
    serialCommand = "L" + str(i) + "_R"
  serialCommand += "\n"
  
  for j in range(len(serialCommand)):
    ser.write(bytes(serialCommand[j], encoding='utf-8'))
