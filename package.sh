rm CoreScope-2.0.tar.gz
rm -rf CoreScope-2.0
mkdir CoreScope-2.0
cp -R ./{run_*.sh,compile.sh,package.sh,demo.sh,src,./output_demo,library,Makefile,README.txt,*.jar,example_graph.tsv,user_guide.pdf} ./CoreScope-2.0
tar cvzf CoreScope-2.0.tar.gz --exclude='._*' ./CoreScope-2.0
rm -rf CoreScope-2.0
echo done.