package com.droiwolf.tiny;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/*
 * Copyright (c) 2014 droidwolf(droidwolf2006@gmail.com)
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class Tiny {
	static final int OP = 0, NUM = 1;
	private static List<String> lexer(String input){return  Stream.of(input.split(" ")).map(String::trim).filter(s -> s.length() > 0).collect(Collectors.toList());}
	private static class Parser {
		Iterator<String> lex;
		String next=null;
		public Parser(List<String> lex) { this.lex=lex.iterator();  }
		private Node parseOp(String str) {
			Node n = new Node(str, OP);
			while (lex.hasNext())	n.addLast(parse());
			return n;
		}
		public Node parse() { return (next=lex.next()).matches("\\d+") ? new Node(Integer.parseInt(next), NUM) : parseOp(next); }
	}
	private static class CodeGenerator {
		final static Map<String, String> opMap = new HashMap<String, String>(4) { { put("sum", "+"); put("sub", "-"); put("div", "/"); put("mul", "*"); } };
		public String generate(Node ast) { return ast.type == NUM ? String.valueOf(ast.val) : genOp(ast); }
		private String genOp(Node node) { return "(" + node.stream().map(n -> new CodeGenerator().generate(n)) .collect(Collectors.joining(" " + opMap.get(node.val) + " ")) + ")"; }
	}
	private static class Node  extends ArrayDeque<Node>{
		Object val; 
		int type;
		public Node(Object val, int type) {super(); this.val = val; this.type = type;}
	}
	private static int eval(Node ast) { return (int) (ast.type == NUM ? ast.val : ast.stream().reduce(evalOps.get(ast.val)).get().val); }
	final static Map<String,BinaryOperator<Node>> evalOps=new HashMap<String,BinaryOperator<Node>>(4) {{
		put("sum", (a, b) -> new Node(eval(a) + eval(b), NUM)); put("sub", (a, b) -> new Node(eval(a) - eval(b), NUM));
		put("div", (a, b) -> new Node(eval(a) / eval(b), NUM)); put("mul", (a, b) -> new Node(eval(a) * eval(b), NUM));}};
		
	public static void main(String[] args) {
		String input = "mul 3 sub 2 sum 1 3 4";
		System.out.println(new CodeGenerator().generate(new Parser(lexer(input)).parse()));
		System.out.println(eval(new Parser(lexer(input)).parse()));
	}
}
