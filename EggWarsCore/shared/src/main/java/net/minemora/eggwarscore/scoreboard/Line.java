package net.minemora.eggwarscore.scoreboard;

import java.util.concurrent.ThreadLocalRandom;

import net.minemora.eggwarscore.utils.RandomString;

public class Line {
	
	private boolean dynamic = false;
	private boolean individual = false;
	private int index;
	private String text;
	private String teamName;
	private static RandomString randomString = new RandomString(8, ThreadLocalRandom.current());

	public Line(String text, int index) {
		this.text = text;
		this.index = index;
		this.teamName = randomString.nextString();
	}
	
	public boolean isDynamic() {
		return dynamic;
	}
	
	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public boolean isIndividual() {
		return individual;
	}

	public void setIndividual(boolean individual) {
		this.individual = individual;
	}

	public String getTeamName() {
		return teamName;
	}

	public int getIndex() {
		return index;
	}
}