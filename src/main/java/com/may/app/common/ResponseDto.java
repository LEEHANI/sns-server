package com.may.app.common;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ResponseDto {
	@Data
	@NoArgsConstructor
	public static class Delete implements Serializable {
		private static final long serialVersionUID = 5727152343505716865L;
		private Boolean data;
		public Delete(Boolean result) {
			this.data = result;
		}
	}
	
	@Data
	@NoArgsConstructor
	public static class Good {
		private Boolean data;
		public Good(Boolean result) {
			this.data = result;
		}
	}
	
	@Data
	@NoArgsConstructor
	public static class UnGood {
		private Boolean data;
		public UnGood(Boolean result) {
			this.data = result;
		}
	}
}
