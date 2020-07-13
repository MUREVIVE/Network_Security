package test;

public class Caesar {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String PlanText = "The quick brown fox jumps over the lazy dog.";
		String CiperText = Caesar(PlanText,2,"encrypt");
		String rst = Caesar(CiperText,2,"decrypt");
		
		System.out.println(PlanText);
		System.out.println(CiperText);
		System.out.println(rst);
	}
	
	public static String Caesar(String plan, int Key, String mode) {
		String word = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String Plan = plan.toUpperCase();
		String result = "";

		if (mode.equals("decrypt"))
			Key = (word.length()- Key)%word.length();
		else if (mode.equals("encrypt"))
			Key = Key%word.length();
		else
			return "err";	
		// 코드 작성하기
		//
		//
		for(int i=0; i<plan.length(); i++)
		{
			char ch = plan.charAt(i);
			if(Character.isLowerCase(ch))
				ch = (char)((ch - 'a' + Key) % word.length() + 'a');
			else if(Character.isUpperCase(ch))
				ch = (char)((ch - 'A' + Key) % word.length() + 'A');
			
			result += ch;
		}
		return result;

	}
	
}


