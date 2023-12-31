package com.unitvectory.auth.sign.model;

public interface JsonWebKey {

	String getKty();

	String getKid();

	String getUse();

	String getAlg();

	String getN();

	String getE();
}
