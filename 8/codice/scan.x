const MAXLENFILE = 2190;
const MAXLENDIR = 2186;
struct Dir_input{ char dir[MAXLENDIR]; int soglia;};
struct File_input{ char file[MAXLENFILE];};
struct File_res{ int chars; int words; int lines;};
program SCANPROG {
	version SCANVERS {
	File_res FILE_SCAN(File_input) = 1;
	int DIR_SCAN(Dir_input) = 2;
	} = 1;
} = 0x20000020;
