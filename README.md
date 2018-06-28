### 从命令行创建一个新的仓库

~~~
touch README.md
git init
git add README.md
git commit -m "first commit"
git remote add origin http://47.98.130.212:3001/Cat/MultiTypeRecyclerView.git
git push -u origin master
~~~

### 从命令行推送已经创建的仓库
~~~
git remote add origin http://47.98.130.212:3001/Cat/MultiTypeRecyclerView.git
git push -u origin master
~~~