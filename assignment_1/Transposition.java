
public class Transposition {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String PlanText = "Common sense is not so common.";
		String rst = transposition_encrypt(PlanText, 8);

		System.out.println(PlanText);
		System.out.println(rst);
	}
	
	public static String transposition_encrypt(String plan, int Key) {
		int col = 0;
		String result = "";
		String row;
		// 코드 작성하기
		//
		//
		while(col != Key) // col이 Key값이 될 때 까지 
		{
			row = ""; // 각 col마다 row값 초기화
			for(int i=col; i<plan.length(); i+=Key)
				row += plan.charAt(i);
				
			result += row;
			col++;
		}
		
		return result;
	}

}
