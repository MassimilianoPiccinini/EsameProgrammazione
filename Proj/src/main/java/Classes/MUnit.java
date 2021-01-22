package Classes;

import com.esameprogrammazione.Proj.Variables;

import Classes.MUnit.unit;

public class MUnit {
	
	public enum unit{
		C,
		F,
		K
	}
	
	public unit un;
	
	public MUnit(unit un) {
		this.un = un;
	}

	public MUnit(String string) {
		if (string.length() > 0) {
			char ch = string.charAt(0);
			switch (Character.toUpperCase(ch)) {
			case 'F':
				this.un = unit.F;
				break;
			case 'K':
				this.un = unit.K;
				break;
			default:
				this.un = unit.C;
				break;
			}
		}else {
			this.un = unit.C;
		}
	}
}
