## 方法1： 通过Java程序， 批量推送Jar文件到 Nexus私有库里 ##
maven run App
curl -v -u admin:admin123 -F "maven2.generate-pom=false" -F "maven2.asset1=@/absolute/path/to/the/local/file/pom.xml" -F "maven2.asset1.extension=pom" -F "maven2.asset2=@/absolute/path/to/the/local/file/product-1.0.0.jar;type=application/java-archive" -F "maven2.asset2.extension=jar" "http://localhost:8081/service/rest/v1/components?repository=maven-releases"

## 方法2： 通过Liunx脚本， 批量推送Jar文件到 Nexus私有库里 ##
当nexus3为HTTP时，使用如下方式：
如你需要上传utils-1.0.jar包，首先需准备新建一个pom.xml文件

<project>
  <modelVersion>4.0.0</modelVersion>
  <groupId>org.foo</groupId>
  <artifactId>utils</artifactId>
  <version>1</version>
</project>

# 上传文件·(pom&jar要一起上传)
curl -v -u admin:admin123 --upload-file pom.xml http://localhost:8081/nexus/repository/maven-releases/org/foo/utils/1.0/utils-1.0.pom
curl -v -u admin:admin123 --upload-file utils-1.0.jar http://localhost:8081/nexus/repository/maven-releases/org/foo/utils/1.0/utils-1.0.jar

# 前提条件： 在Project Home目录下创建Repo子目录，把要上传到Nexus的Jar放进去
./import-jar.sh -u admin -p admin123 -r http://ip:port/repository/myrepo/

如果mavenimport.sh是在Windows编辑后导入Linux的会有问题
使用vi mavenimport.sh进入后底部执行：set ff 看下.sh的格式
什么原因呢， 我们有理由怀疑是文件格式问题？ 我们用vim mavenimport.sh进入mavenimport.sh这个文件， 然后在底部模式下， 执行:set ff查看一下， 结果发现fileformat=dos, 看看， 果然是文件格式问题， 那怎么解决呢？

方法一：vim mavenimport.sh进入mavenimport.sh后， 在底部模式下， 执行:set fileformat=unix后执行:x或者:wq保存修改。 然后就可以执行./mavenimport.sh运行脚本了。（我亲自试过， 是ok的）
方法二：直接执行sed -i "s/\r//" mavenimport.sh来转化， 然后就可以执行./mavenimport.sh运行脚本了。（我亲自试过， 是ok的）
方法三：直接执行dos2unix mavenimport.sh来转化， 然后就可以执行./mavenimport.sh运行脚本了。（我的linux上执行dos2unix ./mavenimport.sh失败， 但是不要放弃啊， 加个busybox就可以了）， 
