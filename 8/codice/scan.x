struct File_res{ int chars; int words; int lines;};
struct Dir_input{ string dir <>; int soglia;};
program SCANPROG {
	version SCANVERS {
	File_res FILE_SCAN(string) = 1;
	int DIR_SCAN(Dir_input) = 2;
	} = 1;
} = 0x20000020;
