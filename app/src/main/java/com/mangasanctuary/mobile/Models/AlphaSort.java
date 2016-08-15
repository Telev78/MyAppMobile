package com.mangasanctuary.mobile.Models;

import java.util.Comparator;

public class AlphaSort implements Comparator<Statut> {

	public int compare(Statut lhs, Statut rhs) {
		if (lhs == Statut.Tous)
			return -1;
		else if (rhs == Statut.Tous)
			return 1;
		else
			return lhs.toString().compareToIgnoreCase(rhs.toString());
	}

}
