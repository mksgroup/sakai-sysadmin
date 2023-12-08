package org.sakaiproject.sysadmin.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Question {
	private Double no;
	private Object question;
	private String lever;
	private Double mark;
	private Boolean isNotRandom;
	private String questionType;
	private String answer;
	private String a;
	private String b;
	private String c;
	private String d;
	private String e;
	private String feedback_A;
	private String feedback_B;
	private String feedback_C;
	private String feedback_D;
	private String feedback_E;
	private String correctAwnserFB;
	private String inCorrectAwnserFB;
	private String objective;
	
	public void setDataField(Integer index, Object data) {
		switch (index) {
		case 0:
			this.no = (Double)data;
			break;
		case 1:
			this.question = data;
			break;
		case 2:
			this.lever = (String)data;
			break;
		case 3:
			this.mark = (Double)data;
			break;
		case 4:
			this.isNotRandom = data.equals("x");
			break;
		case 5:
			this.questionType = (String)data;
			break;
		case 6:
			this.answer = (String)data;
			break;
		case 7:
			this.a = (String)data;
			break;
		case 8:
			this.b = (String)data;
			break;
		case 9:
			this.c = (String)data;
			break;
		case 10:
			this.d = (String)data;
			break;
		case 11:
			this.e = (String)data;
			break;
		case 12:
			this.feedback_A = (String)data;
			break;
		case 13:
			this.feedback_B = (String)data;
			break;
		case 14:
			this.feedback_C = (String)data;
			break;
		case 15:
			this.feedback_D = (String)data;
			break;
		case 16:
			this.feedback_E = (String)data;
			break;
		case 17:
			this.correctAwnserFB = (String)data;
			break;
		case 18:
			this.inCorrectAwnserFB = (String)data;
			break;
		case 19:
			this.objective = (String)data;
			break;
		default:
			throw new IllegalArgumentException("Index out of bound!");
		}
	}
	public Object getDataField(Integer index) {
		switch (index) {
			case 0:
				return this.no;
			case 1:
				return this.question;
			case 2:
				return this.lever;
			case 3:
				return this.mark;
			case 4:
				return this.isNotRandom == null || this.isNotRandom ? "x" : null;
			case 5:
				return this.questionType;
			case 6:
				return this.answer;
			case 7:
				return this.a;
			case 8:
				return this.b;
			case 9:
				return this.c;
			case 10:
				return this.d;
			case 11:
				return this.e;
			case 12:
				return this.feedback_A;
			case 13:
				return this.feedback_B;
			case 14:
				return this.feedback_C;
			case 15:
				return this.feedback_D;
			case 16:
				return this.feedback_E;
			case 17:
				return this.correctAwnserFB;
			case 18:
				return this.inCorrectAwnserFB;
			case 19:
				return this.objective;
			default:
				throw new IllegalArgumentException("Index out of bound!");
		}
	}
}

