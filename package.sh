rm CoreScope-1.0.tar.gz
rm -rf CoreScope-1.0
mkdir CoreScope-1.0
cp -R ./{run_*.sh,compile.sh,package.sh,demo.sh,src,./output_demo,library,Makefile,README.txt,*.jar,example_graph.tsv,user_guide.pdf} ./CoreScope-1.0
tar cvzf CoreScope-1.0.tar.gz --exclude='._*' ./CoreScope-1.0
rm -rf CoreScope-1.0
echo done.