%{

#include <stdio.h>
#include <string.h>
#include "A1.tab.h"

%}

%%

[\n \t \r] {}
"//".* {}

"+" { return ADD; }
"-" { return SUB; }
"*" { return MUL; }
"/" { return DIV; }

";" { return SEMICOLON; }

"&&" { return AND; }
"||" { return OR; }
"!=" { return NE; }
"<=" { return LE; }

"." { return DOT; }

"(" { return LPAREN; }
")" { return RPAREN; }
"{" { return LBRACE; }
"}" { return RBRACE; }
"[" { return LSQR; }
"]" { return RSQR; }

"=" { return ASSGN; }

"#defineStmt" { return DefineStmt; }
"#defineStmt0" { return DefineStmt0; }
"#defineStmt1" { return DefineStmt1; }
"#defineStmt2" { return DefineStmt2; }

"#defineExpr" { return DefineExpr; }
"#defineExpr0" { return DefineExpr0; }
"#defineExpr1" { return DefineExpr1; }
"#defineExpr2" { return DefineExpr2; }

"class" { return CLASS; }
"public" { return PUBLIC; }
"static" { return STATIC; }
"extends" { return EXTENDS; }
"void" { return VOID; }
"main" { return MAIN; }

"!" { return NOT; }
"," { return COMMA; }

"if" {return IF;}
"else" { return ELSE; }
"while" { return WHILE; }
"length" { return LENGTH; }
"true" { return BOOLEAN_TRUE; }
"false" { return BOOLEAN_FALSE; }
"new" { return NEW; }
"this" { return THIS; }
"System.out.println" { return SOP; }

"int" { return INT; }
"boolean" { return BOOLEAN; }
"String" { return STRING; }

"return" { return RETURN; }

[a-zA-Z_][a-zA-Z0-9_]* { 
	int len = strlen(yytext);
	yylval.text = (char*) malloc((len+1)*sizeof(char));
	yylval.text[0] = '\0';
	strcat(yylval.text, yytext);
	return IDENTIFIER; 
}
[0-9]* {
	int len = strlen(yytext);
	yylval.text = (char*) malloc((len+1)*sizeof(char));
	yylval.text[0] = '\0';
	strcat(yylval.text, yytext);
	return INTEGER_LITERAL; 
}

. { printf("Mystery character %s\n", yytext); }


%%