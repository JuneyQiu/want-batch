package com.want.batch.job.lds.bo;

import java.util.Comparator;

public class UpdatableComparator implements Comparator<Updatable> {

	@Override
	public int compare(Updatable arg0, Updatable arg1) {

		if ((arg0 == null) || (arg1 == null) || (arg0.getDn() == null) || (arg1.getDn() == null)) {
			return -1;
		}
		return arg0.getDn().compareTo(arg1.getDn());
	}

}
