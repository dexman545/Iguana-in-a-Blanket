package dex.iguanablanket.impl;


import dex.iguanablanket.IguanaBlanket;
import dex.iguanablanket.config.IguanaConfig;

public interface ItemWeight {
	IguanaConfig cfg = IguanaBlanket.cfg;

	float getWeight();

	float getSingleWeight();
}

