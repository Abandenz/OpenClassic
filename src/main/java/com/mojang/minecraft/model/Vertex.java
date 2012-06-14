package com.mojang.minecraft.model;

import com.mojang.minecraft.model.ModelPoint;

public final class Vertex {

	public ModelPoint a;
	public float b;
	public float c;

	public Vertex(float var1, float var2, float var3, float var4, float var5) {
		this(new ModelPoint(var1, var2, var3), var4, var5);
	}

	public final Vertex a(float var1, float var2) {
		return new Vertex(this, var1, var2);
	}

	private Vertex(Vertex var1, float var2, float var3) {
		this.a = var1.a;
		this.b = var2;
		this.c = var3;
	}

	private Vertex(ModelPoint var1, float var2, float var3) {
		this.a = var1;
		this.b = var2;
		this.c = var3;
	}
}
