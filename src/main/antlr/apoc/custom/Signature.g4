grammar Signature;

procedure:	namespace? name '(' (parameter',')*(parameter)? ')' '::'  results ;
function:	namespace? name '(' (parameter',')*(parameter)? ')' '::'  (type | '(' type ')');
results:	empty | '(' (result',')*(result) ')' ;
parameter:	name ('=' defaultValue)? '::' type ;
result:	name '::' type ;
namespace:	(name'.')+ ;
name:	IDENTIFIER | QUOTED_IDENTIFIER ;
empty:	'VOID' ;
type:	opt_type | list_type ;
defaultValue: value;

list_type:	'LIST''?'?' OF '+opt_type ;
opt_type:	base_type'?'? ;
base_type:	'MAP' | 'ANY' | 'NODE' | 'REL' | 'RELATIONSHIP' | 'EDGE' | 'PATH' | 'NUMBER' | 'LONG' | 'INT' | 'INTEGER' | 'FLOAT' | 'DOUBLE' | 'BOOL' | 'BOOLEAN' | 'DATE' | 'TIME' | 'LOCALTIME' | 'DATETIME' | 'LOCALDATETIME' | 'DURATION' | 'POINT' | 'GEO' | 'GEOMETRY' | 'STRING' | 'TEXT' ;
NEWLINE:	[\r\n]+ ;
QUOTED_IDENTIFIER:	'`' [^`]+? '`' ;
IDENTIFIER:	[a-zA-Z_][a-zA-Z0-9_]+ ;
WS:	[ \t\r\n]+ -> skip ;
value: nullValue | INT_VALUE | FLOAT_VALUE | boolValue | mapValue | listValue | stringValue;
INT_VALUE: [0-9]+;
FLOAT_VALUE: ([0-9]+'.'[0-9]+) | 'NaN';
boolValue: 'true'|'false';
stringValue: QUOTED_STRING_VALUE | PLAIN_STRING_VALUE;
QUOTED_STRING_VALUE: '"'[^"]+?'"';
PLAIN_STRING_VALUE: .+?;
nullValue: 'null';
listValue: '[' ((value',')*value)?']';
mapValue: '{' (((name ':' value)',')*(name ':' value) | ((name '=' value)',')*(name '=' value))? '}';
