package com.projetjee.gc;



public final static class MainApp extends GestCommercialeApplication {

    private int x;
    private int y;
    private int z;
    private int t;
    private int u;
    private int v;
    private int w;
    private int a;
    private int b;
    private int c;
    private int d;
    private int e;
    private int f;
    private int g;
    private int h;
    private int i;

    public static void main(String[] args)
    {
        System.out.println("Hello World!");
    }


    public void badSmellMethod(int x, int y, int z, int t, int u, int v, int w, int a, int b, int c, int d, int e, int f, int g, int h, int i)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.t = t;
        this.u = u;
        this.v = v;
        this.w = w;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
        this.f = f;
        this.g = g;
        this.h = h;
        this.i = i;
        x = y;
        y = z;
        z = t;
        t = u;
        u = v;
        v = w;
        w = a;
        a = b;
        b = c;
        c = d;
        d = e;
        e = f;
        f = g;
        g = h;
        h = i;

        i = h + g;
        h = g + f;
        g = f + e;
        f = e + d;
        e = d + c;
        d = c + b;
        c = b + a;
        b = a + w;
        a = w + v;
        w = v + u;
        v = u + t;
        u = t + z;
        t = z + y;
        z = y + x;
        y = x + i;
        x = i + h;

        h = i - g;
        g = h - f;
        f = g - e;
        e = f - d;
        d = e - c;
        c = d - b;
        b = c - a;
        a = b - w;
        w = a - v;
        v = w - u;
        u = v - t;
        t = u - z;
        z = t - y;
        y = z - x;
        x = y - i;
        i = x - h;

        h = i * g * f;
        g = h * f * e;
        f = g * e * d;
        e = f * d * c;
        d = e * c * b;
        c = d * b * a;
        b = c * a * w;
        a = b * w * v;
        w = a * v * u;
        v = w * u * t;
        u = v * t * z;
        t = u * z * y;
        z = t * y * x;
        y = z * x * i;
        x = y * i * h;
        i = x * h * g;

    }
}
