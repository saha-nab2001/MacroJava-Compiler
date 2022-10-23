%{
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

// Helper function for transform(char*, char*, char*).
char* repIfFound(int start, char* pattern, char* text, char* repPattern) {
    int patLen = strlen(pattern);
    int texLen = strlen(text);
    int repLen = strlen(repPattern);
    /****************************************************************************/
    if(start > 0 && ((text[start-1] >= 'a') && (text[start-1] <= 'z')) || ((text[start-1] >= 'A') && (text[start-1] <= 'Z')) || ((text[start-1] >= '0') && (text[start-1] <= '9')) || (text[start-1] == '_')) {
        return text;
    }
    if(start + patLen < texLen && ((text[start+patLen] >= 'a') && (text[start+patLen] <= 'z')) || ((text[start+patLen] >= 'A') && (text[start+patLen] <= 'Z')) || ((text[start+patLen] >= '0') && (text[start+patLen] <= '9')) || (text[start+patLen] == '_')) {
    	return text;
    } 
    /****************************************************************************/
    for(int i = 0; i < patLen; ++i) {
        if(pattern[i] != text[i+start]) {
            return text;
        }
    }
    char* temp = (char*) calloc((patLen+texLen+repLen+3), sizeof(char));
    for(int j = 0; j < start; ++j) {
        temp[j] = text[j];   
    }
    for(int j = 0; j < repLen; ++j) {
        temp[j+start] = repPattern[j];
    }
    for(int j = 0; j + start+patLen < texLen; ++j) {
    	//printf("%c ", text[j+ start+patLen]);
		temp[j+start+repLen] = text[j+ start+patLen];
    }
    return temp;
}

// Replaces all arguments with the respective passed parameters in the macro expressions/statements
char* transform(char* argsList, char* text, char* modifiedArgsList) {
	char* token = strtok_r(argsList, ",", &argsList);
	int texLen = strlen(text);
	char* tokenModified = strtok_r(modifiedArgsList, ",", &modifiedArgsList);
    while (token != NULL) {
        int tokLen = strlen(token);
        for(int i = 0; i + tokLen - 1 < texLen; ++i) {
        	text = repIfFound(i, token, text, tokenModified);
        }
        token = strtok_r(argsList, ",", &argsList);
        tokenModified = strtok_r(modifiedArgsList, ",", &modifiedArgsList);
    }
    return text;
}

// Linked list for macro expressions
struct exprNode {
    char* id;
    char* args;
    char* expr;
    struct exprNode* next; 
}; 

struct exprNode* exprHead = NULL;

// Inserts a new macro expression into the list
void insertExpr(char* _id, char* _args, char* _expr) {
    struct exprNode* temp = (struct exprNode*) malloc(sizeof(struct exprNode));
    temp->id = _id;
    temp->args = _args;
    temp->expr = _expr;
    temp->next = exprHead;
    exprHead = temp; 
}

// Searches for an existing macro expression in the list
char* searchExpr(char* _id, char* _args) {
    struct exprNode* temp = exprHead;
    while(temp != NULL) {
        if((strcmp(temp->id, _id) == 0)) {
            if(strcmp(_args, "") == 0) 
                return temp->expr;
            char* retText = transform(temp->args, temp->expr, _args);
            return retText;
        }
        temp = temp->next;
    }
    return "Not present";
}

// Linked list for macro statements
struct stmtNode {
    char* id;
    char* args;
    char* stmt;
    struct stmtNode* next; 
}; 

struct stmtNode* stmtHead = NULL;

// Inserts a new macro statement into the list
void insertStmt(char* _id, char* _args, char* _stmt) {
    struct stmtNode* temp = (struct stmtNode*) malloc(sizeof(struct stmtNode));
    temp->id = _id;
    temp->args = _args;
    temp->stmt = _stmt;
    temp->next = stmtHead;
    stmtHead = temp; 
}

// Searches for an existing macro statement in the list
char* searchStmt(char* _id, char* _args) {
    struct stmtNode* temp = stmtHead;
    while(temp != NULL) {
        if((strcmp(temp->id, _id) == 0)) {
            if(strcmp(_args, "") == 0) 
                return temp->stmt;
            char* retText = transform(temp->args, temp->stmt, _args);
            return retText;
        }
        temp = temp->next;
    }
    return "Not present";
}

// Concatenates 1 string
char* cat1(char* str1) {
    int len1 = strlen(str1);
	char* str = (char*) calloc((len1+1),sizeof(char));
	strcat(str, str1);
    return str;
}

// Concatenates 2 strings
char* cat2(char* str1, char* str2) {
    int len1 = strlen(str1);
    int len2 = strlen(str2);
	char* str = (char*) calloc((len1+len2+2), sizeof(char));
    strcat(str, str1);
    strcat(str, str2);
	return str;
}

// Concatenates 3 strings
char* cat3(char* str1, char* str2, char* str3) {
    int len1 = strlen(str1);
    int len2 = strlen(str2);
    int len3 = strlen(str3);
	char* str = (char*) calloc((len1+len2+len3+3), sizeof(char));
    strcat(str, str1);
    strcat(str, str2);
    strcat(str, str3);
	return str;
}

// Concatenates 4 strings
char* cat4(char* str1, char* str2, char* str3, char* str4) {
    int len1 = strlen(str1);
    int len2 = strlen(str2);
    int len3 = strlen(str3);
    int len4 = strlen(str4);
	char* str = (char*) calloc((len1+len2+len3+len4+4), sizeof(char));
    strcat(str, str1);
    strcat(str, str2);
    strcat(str, str3);
    strcat(str, str4);
	return str;
}

// Concatenates 5 strings
char* cat5(char* str1, char* str2, char* str3, char* str4, char* str5) {
    int len1 = strlen(str1);
    int len2 = strlen(str2);
    int len3 = strlen(str3);
    int len4 = strlen(str4);
    int len5 = strlen(str5);
	char* str = (char*) calloc((len1+len2+len3+len4+len5+5), sizeof(char));
    strcat(str, str1);
    strcat(str, str2);
    strcat(str, str3);
    strcat(str, str4);
    strcat(str, str5);
	return str;
}

// Concatenates 6 strings
char* cat6(char* str1, char* str2, char* str3, char* str4, char* str5, char* str6) {
    int len1 = strlen(str1);
    int len2 = strlen(str2);
    int len3 = strlen(str3);
    int len4 = strlen(str4);
    int len5 = strlen(str5);
    int len6 = strlen(str6);
	char* str = (char*) calloc((len1+len2+len3+len4+len5+len6+6), sizeof(char));
    strcat(str, str1);
    strcat(str, str2);
    strcat(str, str3);
    strcat(str, str4);
    strcat(str, str5);
    strcat(str, str6);
	return str;
}

// Concatenates 7 strings
char* cat7(char* str1, char* str2, char* str3, char* str4, char* str5, char* str6, char* str7) {
    int len1 = strlen(str1);
    int len2 = strlen(str2);
    int len3 = strlen(str3);
    int len4 = strlen(str4);
    int len5 = strlen(str5);
    int len6 = strlen(str6);
    int len7 = strlen(str7);
	char* str = (char*) calloc((len1+len2+len3+len4+len5+len6+len7+7), sizeof(char));
    strcat(str, str1);
    strcat(str, str2);
    strcat(str, str3);
    strcat(str, str4);
    strcat(str, str5);
    strcat(str, str6);
    strcat(str, str7);
	return str;
}

// Concatenates 8 strings
char* cat8(char* str1, char* str2, char* str3, char* str4, char* str5, char* str6, char* str7, char* str8) {
    int len1 = strlen(str1);
    int len2 = strlen(str2);
    int len3 = strlen(str3);
    int len4 = strlen(str4);
    int len5 = strlen(str5);
    int len6 = strlen(str6);
    int len7 = strlen(str7);
    int len8 = strlen(str8);
	char* str = (char*) calloc((len1+len2+len3+len4+len5+len6+len7+len8+8), sizeof(char));
    strcat(str, str1);
    strcat(str, str2);
    strcat(str, str3);
    strcat(str, str4);
    strcat(str, str5);
    strcat(str, str6);
    strcat(str, str7);
    strcat(str, str8);
	return str;
}

// Concatenates 9 strings
char* cat9(char* str1, char* str2, char* str3, char* str4, char* str5, char* str6, char* str7, char* str8, char* str9) {
    int len1 = strlen(str1);
    int len2 = strlen(str2);
    int len3 = strlen(str3);
    int len4 = strlen(str4);
    int len5 = strlen(str5);
    int len6 = strlen(str6);
    int len7 = strlen(str7);
    int len8 = strlen(str8);
    int len9 = strlen(str9);
	char* str = (char*) calloc((len1+len2+len3+len4+len5+len6+len7+len8+len9+9), sizeof(char));
    strcat(str, str1);
    strcat(str, str2);
    strcat(str, str3);
    strcat(str, str4);
    strcat(str, str5);
    strcat(str, str6);
    strcat(str, str7);
    strcat(str, str8);
    strcat(str, str9);
	return str;
}

// Concatenates 10 strings
char* cat10(char* str1, char* str2, char* str3, char* str4, char* str5, char* str6, char* str7, char* str8, char* str9, char* str10) {
    int len1 = strlen(str1);
    int len2 = strlen(str2);
    int len3 = strlen(str3);
    int len4 = strlen(str4);
    int len5 = strlen(str5);
    int len6 = strlen(str6);
    int len7 = strlen(str7);
    int len8 = strlen(str8);
    int len9 = strlen(str9);
    int len10 = strlen(str10);
	char* str = (char*) calloc((len1+len2+len3+len4+len5+len6+len7+len8+len9+len10+10), sizeof(char));
    strcat(str, str1);
    strcat(str, str2);
    strcat(str, str3);
    strcat(str, str4);
    strcat(str, str5);
    strcat(str, str6);
    strcat(str, str7);
    strcat(str, str8);
    strcat(str, str9);
    strcat(str, str10);
	return str;
}

// Concatenates 11 strings
char* cat11(char* str1, char* str2, char* str3, char* str4, char* str5, char* str6, char* str7, char* str8, char* str9, char* str10, char* str11) {
    int len1 = strlen(str1);
    int len2 = strlen(str2);
    int len3 = strlen(str3);
    int len4 = strlen(str4);
    int len5 = strlen(str5);
    int len6 = strlen(str6);
    int len7 = strlen(str7);
    int len8 = strlen(str8);
    int len9 = strlen(str9);
    int len10 = strlen(str10);
    int len11 = strlen(str11);
	char* str = (char*) calloc((len1+len2+len3+len4+len5+len6+len7+len8+len9+len10+len11+11), sizeof(char));
    strcat(str, str1);
    strcat(str, str2);
    strcat(str, str3);
    strcat(str, str4);
    strcat(str, str5);
    strcat(str, str6);
    strcat(str, str7);
    strcat(str, str8);
    strcat(str, str9);
    strcat(str, str10);
    strcat(str, str11);
	return str;
} 

// Concatenates 12 strings
char* cat12(char* str1, char* str2, char* str3, char* str4, char* str5, char* str6, char* str7, char* str8, char* str9, char* str10, char* str11, char* str12) {
    int len1 = strlen(str1);
    int len2 = strlen(str2);
    int len3 = strlen(str3);
    int len4 = strlen(str4);
    int len5 = strlen(str5);
    int len6 = strlen(str6);
    int len7 = strlen(str7);
    int len8 = strlen(str8);
    int len9 = strlen(str9);
    int len10 = strlen(str10);
    int len11 = strlen(str11);
    int len12 = strlen(str12);
	char* str = (char*) calloc((len1+len2+len3+len4+len5+len6+len7+len8+len9+len10+len11+len12+12), sizeof(char));
    strcat(str, str1);
    strcat(str, str2);
    strcat(str, str3);
    strcat(str, str4);
    strcat(str, str5);
    strcat(str, str6);
    strcat(str, str7);
    strcat(str, str8);
    strcat(str, str9);
    strcat(str, str10);
    strcat(str, str11);
    strcat(str, str12);
	return str;
}

// Concatenates 13 strings
char* cat13(char* str1, char* str2, char* str3, char* str4, char* str5, char* str6, char* str7, char* str8, char* str9, char* str10, char* str11, char* str12, char* str13) {
    int len1 = strlen(str1);
    int len2 = strlen(str2);
    int len3 = strlen(str3);
    int len4 = strlen(str4);
    int len5 = strlen(str5);
    int len6 = strlen(str6);
    int len7 = strlen(str7);
    int len8 = strlen(str8);
    int len9 = strlen(str9);
    int len10 = strlen(str10);
    int len11 = strlen(str11);
    int len12 = strlen(str12);
    int len13 = strlen(str13);
	char* str = (char*) calloc((len1+len2+len3+len4+len5+len6+len7+len8+len9+len10+len11+len12+len13+13), sizeof(char));
    strcat(str, str1);
    strcat(str, str2);
    strcat(str, str3);
    strcat(str, str4);
    strcat(str, str5);
    strcat(str, str6);
    strcat(str, str7);
    strcat(str, str8);
    strcat(str, str9);
    strcat(str, str10);
    strcat(str, str11);
    strcat(str, str12);
    strcat(str, str13);
	return str;
}

// Concatenates 14 strings
char* cat14(char* str1, char* str2, char* str3, char* str4, char* str5, char* str6, char* str7, char* str8, char* str9, char* str10, char* str11, char* str12, char* str13, char* str14) {
    int len1 = strlen(str1);
    int len2 = strlen(str2);
    int len3 = strlen(str3);
    int len4 = strlen(str4);
    int len5 = strlen(str5);
    int len6 = strlen(str6);
    int len7 = strlen(str7);
    int len8 = strlen(str8);
    int len9 = strlen(str9);
    int len10 = strlen(str10);
    int len11 = strlen(str11);
    int len12 = strlen(str12);
    int len13 = strlen(str13);
    int len14 = strlen(str14);
	char* str = (char*) calloc((len1+len2+len3+len4+len5+len6+len7+len8+len9+len10+len11+len12+len13+len14+14), sizeof(char));
    strcat(str, str1);
    strcat(str, str2);
    strcat(str, str3);
    strcat(str, str4);
    strcat(str, str5);
    strcat(str, str6);
    strcat(str, str7);
    strcat(str, str8);
    strcat(str, str9);
    strcat(str, str10);
    strcat(str, str11);
    strcat(str, str12);
    strcat(str, str13);
    strcat(str, str14);
	return str;
}

%}

%union{
    char* text;
}

%token ADD SUB MUL DIV SEMICOLON AND OR NE LE DOT LPAREN RPAREN LBRACE RBRACE LSQR RSQR ASSGN CLASS
%token DefineStmt DefineStmt0 DefineStmt1 DefineStmt2
%token DefineExpr DefineExpr0 DefineExpr1 DefineExpr2
%token PUBLIC STATIC EXTENDS VOID MAIN NOT COMMA IF ELSE WHILE LENGTH BOOLEAN_TRUE BOOLEAN_FALSE NEW THIS SOP
%token INT BOOLEAN STRING IDENTIFIER INTEGER_LITERAL RETURN

%type<text> IDENTIFIER INTEGER_LITERAL Identifier Integer MacroDefExpression CommaIdentifierStar Expression 
            MacroDefStatement Statement StatementStar MacroDefinition PrimaryExpression CommaExpressionStar Type MethodDeclaration
            TypeIdentifierSemicolonStar CommaTypeIdentifierStar TypeDeclaration MethodDeclarationStar MainClass MacroDefinitionStar
            TypeDeclarationStar MethodContent Goal

%%

Goal : MacroDefinitionStar MainClass TypeDeclarationStar
{
    $$ = cat3($2, "\n\n", $3);
    printf("%s\n", $$);
};

MainClass : CLASS Identifier LBRACE PUBLIC STATIC VOID MAIN LPAREN STRING LSQR RSQR Identifier RPAREN LBRACE SOP LPAREN Expression RPAREN SEMICOLON RBRACE RBRACE
{
    $$ = cat7(" class ", $2, "{ \n\t public static void main (String [] ", $12, "){ \n\t\t System.out.println(", $17,"); \n\t } \n }");
};

TypeDeclaration : CLASS Identifier LBRACE TypeIdentifierSemicolonStar MethodDeclarationStar RBRACE
{
    $$ = cat7(" class ", $2, "{ \n\t", $4, "\n", $5, "\n}");
}
                | CLASS Identifier EXTENDS Identifier LBRACE TypeIdentifierSemicolonStar MethodDeclarationStar RBRACE
{
    $$ = cat9(" class ", $2, " extends ", $4, "{ \n\t", $6, "\n", $7, "}");
};

MethodDeclaration : PUBLIC Type Identifier LPAREN RPAREN LBRACE MethodContent RETURN Expression SEMICOLON RBRACE 
{
    $$ = cat9(" public ", $2, " ", $3, "(){", $7, "\nreturn ", $9, ";}");
} 
                  | PUBLIC Type Identifier LPAREN Type Identifier CommaTypeIdentifierStar RPAREN LBRACE MethodContent RETURN Expression SEMICOLON RBRACE 
{
    $$ = cat14(" public ", $2, " ", $3, "(", $5, " ", $6, $7, "){", $10, "\nreturn ", $12, ";}");
};

MethodContent : TypeIdentifierSemicolonStar 
{
    $$ = cat1($1);
}
              | TypeIdentifierSemicolonStar Statement StatementStar
{
    $$ = cat4($1, "\n", $2, $3);
};

Type : INT LSQR RSQR
{
    $$ = cat1(" int [] ");
}
     | BOOLEAN
{
    $$ = cat1(" boolean ");
}
     | INT
{
    $$ = cat1(" int ");
}
     | Identifier
{
    $$ = cat1($1);
};

Statement : LBRACE StatementStar RBRACE
{
    $$ = cat3("{", $2, "}");
}
          | SOP LPAREN Expression RPAREN SEMICOLON
{
    $$ = cat4(" System.out.println", "(", $3, ");");
}  
          | Identifier ASSGN Expression SEMICOLON
{
    $$ = cat4($1, " = ", $3, ";");
} 
          | Identifier LSQR Expression RSQR ASSGN Expression SEMICOLON
{
    $$ = cat7($1, "[", $3, "]", " = ", $6, ";");
} 
          | IF LPAREN Expression RPAREN Statement
{
    $$ = cat5(" if", "(", $3, ") ", $5);
} 
          | IF LPAREN Expression RPAREN Statement ELSE Statement
{
    $$ = cat7(" if", "(", $3, ") ", $5, "\nelse ", $7);
} 
          | WHILE LPAREN Expression RPAREN Statement
{
    $$ = cat5(" while", "(", $3, ")", $5);
}
          | Identifier LPAREN RPAREN SEMICOLON
{
    char* temp = searchStmt($1, "");
    if(strcmp("Not present", temp) == 0) {
        yyerror();
        exit(0);
    }
    $$ = cat1(temp);
}
          | Identifier LPAREN Expression CommaExpressionStar RPAREN SEMICOLON
{
    char* args = cat2($3, $4);
    char* temp = searchStmt($1, args);
    if(strcmp("Not present", temp) == 0) {
        yyerror();
        exit(0);
    }
    $$ = cat1(temp);
};

Expression : PrimaryExpression AND PrimaryExpression
{
    $$ = cat3($1, " && ", $3);
}
           | PrimaryExpression OR PrimaryExpression
{
    $$ = cat3($1, " || ", $3);
}
           | PrimaryExpression NE PrimaryExpression
{
    $$ = cat3($1, " != ", $3);
}
           | PrimaryExpression LE PrimaryExpression
{
    $$ = cat3($1, " <= ", $3);
}
           | PrimaryExpression ADD PrimaryExpression
{
    $$ = cat3($1, " + ", $3);
}
           | PrimaryExpression SUB PrimaryExpression
{
    $$ = cat3($1, " - ", $3);
}
           | PrimaryExpression MUL PrimaryExpression
{
    $$ = cat3($1, " * ", $3);
}
           | PrimaryExpression DIV PrimaryExpression
{
    $$ = cat3($1, " / ", $3);
}
           | PrimaryExpression LSQR PrimaryExpression RSQR
{
    $$ = cat4($1, "[", $3, "]");
}
           | PrimaryExpression DOT LENGTH
{
    $$ = cat3($1, ".", "length ");
}
           | PrimaryExpression
{
    $$ = cat1($1);
}
           | PrimaryExpression DOT Identifier LPAREN RPAREN
{
    $$ = cat4($1, ".", $3, "()");
}
           | PrimaryExpression DOT Identifier LPAREN Expression CommaExpressionStar RPAREN
{
    $$ = cat7($1, ".", $3, "(", $5, $6, ")");
}
           | Identifier LPAREN RPAREN
{   
    char* temp = searchExpr($1, "");
    if(strcmp("Not present", temp) == 0) {
        yyerror();
        exit(0);
    }
    $$ = cat1(temp);
}
           | Identifier LPAREN Expression CommaExpressionStar RPAREN
{
    char* args = cat2($3, $4);
    char* temp = searchExpr($1, args);
    if(strcmp("Not present", temp) == 0) {
        yyerror();
        exit(0);
    }

    $$ = cat1(temp);   
};

PrimaryExpression : Integer 
{
    $$ = cat1($1);
}
                  | BOOLEAN_TRUE
{
    $$ = cat1("true");
}
                  | BOOLEAN_FALSE
{
    $$ = cat1("false");
}
                  | Identifier 
{
    $$ = cat1($1);
}  
                  | THIS
{
    $$ = cat1("this");
}  
                  | NEW INT LSQR Expression RSQR
{
    $$ = cat4("new int", "[", $4, "]");
}
                  | NEW IDENTIFIER LPAREN RPAREN
{
    $$ = cat3("new ", $2, "()");
}
                  | NOT Expression
{
    $$ = cat2("!", $2);
}  
                  | LPAREN Expression RPAREN 
{
    $$ = cat3("(", $2, ")");
};                 

MacroDefinition : MacroDefExpression 
{
    $$ = cat1($1);
}
                | MacroDefStatement
{
    $$ = cat1($1);
};


MacroDefStatement : DefineStmt Identifier LPAREN Identifier COMMA Identifier COMMA Identifier CommaIdentifierStar RPAREN LBRACE StatementStar RBRACE
{
    $$ = cat13("#defineStmt ", $2, "(", $4, ",", $6, ",", $8, $9, ")", "(", $12, ")");
    char* args = cat6($4, ",", $6, ",", $8, $9);
    insertStmt($2, args, $12);        
}
                  | DefineStmt0 Identifier LPAREN RPAREN LBRACE StatementStar RBRACE
{
    $$ = cat7("#defineExpr0 ", $2, "(", ")", "{", $6, "}");
    char* args = cat1("");
    insertStmt($2, args, $6);
}
                  | DefineStmt1 Identifier LPAREN Identifier RPAREN LBRACE StatementStar RBRACE
{
    $$ = cat8("#defineExpr1 ", $2, "(", $4, ")", "{", $7, "}");
    char* args = cat1($4);
    insertStmt($2, args, $7);
}
                  | DefineStmt2 Identifier LPAREN Identifier COMMA Identifier RPAREN LBRACE StatementStar RBRACE
{
    $$ = cat10("#defineStmt2 ", $2, "(", $4, ",", $6, ")", "{", $9, "}");
    char* args = cat3($4, ",", $6);
    insertStmt($2, args, $9);
};

MacroDefExpression : DefineExpr Identifier LPAREN Identifier COMMA Identifier COMMA Identifier CommaIdentifierStar RPAREN LPAREN Expression RPAREN
{
    $$ = cat13("#defineExpr ", $2, "(", $4, ",", $6, ",", $8, $9, ")", "(", $12, ")");
    char* args = cat6($4, ",", $6, ",", $8, $9);
    insertExpr($2, args, $12);
}
                   | DefineExpr0 Identifier LPAREN RPAREN LPAREN Expression RPAREN
{
    $$ = cat7("#defineExpr0 ", $2, "(", ")", "(", $6, ")");
    char* args = cat1("");
    insertExpr($2, args, $6);
}
                   | DefineExpr1 Identifier LPAREN Identifier RPAREN LPAREN Expression RPAREN
{
    $$ = cat8("#defineExpr1 ", $2, "(", $4, ")", "(", $7, ")");
    char* args = cat1($4);
    insertExpr($2, args, $7);
}
                   | DefineExpr2 Identifier LPAREN Identifier COMMA Identifier RPAREN LPAREN Expression RPAREN
{
    $$ = cat10("#defineExpr2 ", $2, "(", $4, ", ", $6, ")", "(", $9, ")");
    char* args = cat3($4, ",", $6);
    insertExpr($2, args, $9);
};

Integer : INTEGER_LITERAL 
{
    $$ = cat1($1);
};

Identifier : IDENTIFIER 
{
    $$ = cat1($1);
};

StatementStar : StatementStar Statement
{
    $$ = cat3($1, "\n", $2);
}
              | 
{
    $$ = "";
};

CommaIdentifierStar : CommaIdentifierStar COMMA Identifier 
{
    $$ = cat3($1, ",", $3);
}
                    |
{
    $$ = "";
};

CommaExpressionStar : CommaExpressionStar COMMA Expression
{
    $$ = cat3($1, ", ", $3);
}
                    |
{
    $$ = "";
};

TypeIdentifierSemicolonStar : TypeIdentifierSemicolonStar Type Identifier SEMICOLON
{
    $$ = cat6($1, "\n", $2, " ", $3, ";");
}
                            | 
{
    $$ = "";
};

CommaTypeIdentifierStar : CommaTypeIdentifierStar COMMA Type Identifier 
{
    $$ = cat5($1, ", ", $3, " ", $4);
}
                        | 
{
    $$ = "";
};

MethodDeclarationStar : MethodDeclarationStar MethodDeclaration
{
    $$ = cat3($1, "\n", $2);
}
                      | 
{
    $$ = "";
};

MacroDefinitionStar : MacroDefinitionStar MacroDefinition
{
    $$ = cat3($1, "\n", $2);
}
                    | 
{
    $$ = "";
};

TypeDeclarationStar : TypeDeclarationStar TypeDeclaration
{
    $$ = cat3($1, "\n", $2);
}
                    | 
{
    $$ = "";
};

%% 

int yyerror(char *s)
{
	printf("//Failed to parse input code\n");
    exit(0);
	return 0;
}


int main()
{
	yyparse();
	return 0;
}