package com.example.lsmapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Dibuja números (0-9) y letras (A-Z) que flotan hacia arriba con un vaivén suave,
 * girando un poco y desvaneciéndose en los bordes. Va detrás de los botones.
 */
public class NumerosFlotantesView extends View {

    // ====== AJUSTES (cámbialos a tu gusto) ======
    private static final int CANTIDAD = 45;               // cuántos caracteres hay en pantalla
    private static final String NUMEROS = "0123456789";
    private static final String LETRAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final float PROBABILIDAD_NUMERO = 0.5f;  // 0.5 = mitad números, mitad letras
    private static final int COLOR = Color.parseColor("#0A848F"); // turquesa de tu app
    private static final float ALPHA_MIN = 0.15f;         // transparencia mínima (0 a 1)
    private static final float ALPHA_MAX = 0.35f;   // transparencia máxima (0 a 1)
    private static final float VELOCIDAD_MIN_DP = 14f;      // dp por segundo (más lento)
    private static final float VELOCIDAD_MAX_DP = 38f;      // dp por segundo (más rápido)
    private static final float TAMANO_MIN_SP = 18f;
    private static final float TAMANO_MAX_SP = 44f;
    // ============================================

    private static class Particula {
        String texto;
        float xBase, y;
        float velocidadY;      // px por segundo
        float amplitud;        // cuánto se mueve de lado a lado
        float fase, velFase;   // para el vaivén
        float tamano;          // px
        float alpha;
        float rotacion, velRotacion;
        float x;               // posición actual calculada
    }

    private final List<Particula> particulas = new ArrayList<>();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Random random = new Random();

    private float densidad;
    private float densidadFuente;
    private long ultimoTiempo = 0;
    private boolean activo = false;

    public NumerosFlotantesView(Context context) {
        super(context);
        init();
    }

    public NumerosFlotantesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NumerosFlotantesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        densidad = getResources().getDisplayMetrics().density;
        densidadFuente = getResources().getDisplayMetrics().scaledDensity;
        paint.setColor(COLOR);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        setClickable(false);
        setFocusable(false);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        particulas.clear();
        if (w <= 0 || h <= 0) return;

        for (int i = 0; i < CANTIDAD; i++) {
            Particula p = new Particula();
            reiniciar(p, w, h, true);
            particulas.add(p);
        }
    }

    /** Coloca una partícula con valores aleatorios. Si es la primera vez, se reparte en toda la altura. */
    private void reiniciar(Particula p, int w, int h, boolean primeraVez) {
        if (random.nextFloat() < PROBABILIDAD_NUMERO) {
            p.texto = String.valueOf(NUMEROS.charAt(random.nextInt(NUMEROS.length())));
        } else {
            char letra = LETRAS.charAt(random.nextInt(LETRAS.length()));
            // La mitad de las letras en minúscula, como en tu splash
            p.texto = String.valueOf(random.nextBoolean() ? Character.toLowerCase(letra) : letra);
        }
        p.tamano = (TAMANO_MIN_SP + random.nextFloat() * (TAMANO_MAX_SP - TAMANO_MIN_SP)) * densidadFuente;
        p.xBase = random.nextFloat() * w;
        p.y = primeraVez ? random.nextFloat() * h : h + p.tamano;
        p.velocidadY = (VELOCIDAD_MIN_DP + random.nextFloat() * (VELOCIDAD_MAX_DP - VELOCIDAD_MIN_DP)) * densidad;
        p.amplitud = (10f + random.nextFloat() * 22f) * densidad;
        p.fase = random.nextFloat() * (float) (Math.PI * 2);
        p.velFase = 0.4f + random.nextFloat() * 0.8f;
        p.alpha = ALPHA_MIN + random.nextFloat() * (ALPHA_MAX - ALPHA_MIN);
        p.rotacion = random.nextFloat() * 40f - 20f;
        p.velRotacion = random.nextFloat() * 16f - 8f;
        p.x = p.xBase;
    }

    private void actualizar(float dt) {
        int w = getWidth();
        int h = getHeight();
        for (Particula p : particulas) {
            p.y -= p.velocidadY * dt;
            p.fase += p.velFase * dt;
            p.x = p.xBase + (float) Math.sin(p.fase) * p.amplitud;
            p.rotacion += p.velRotacion * dt;

            if (p.y < -p.tamano) {
                reiniciar(p, w, h, false);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        long ahora = System.nanoTime();
        float dt = ultimoTiempo == 0 ? 0f : (ahora - ultimoTiempo) / 1_000_000_000f;
        ultimoTiempo = ahora;
        if (dt > 0.05f) dt = 0.05f; // evita saltos si la app estuvo pausada

        actualizar(dt);

        int h = getHeight();
        for (Particula p : particulas) {
            // Desvanecer cerca del borde superior e inferior
            float t = p.y / h;
            float fade = Math.min(t / 0.15f, (1f - t) / 0.15f);
            fade = Math.max(0f, Math.min(1f, fade));

            paint.setAlpha((int) (255 * p.alpha * fade));
            paint.setTextSize(p.tamano);

            canvas.save();
            canvas.translate(p.x, p.y);
            canvas.rotate(p.rotacion);
            canvas.drawText(p.texto, 0, 0, paint);
            canvas.restore();
        }

        if (activo) {
            postInvalidateOnAnimation();
        }
    }

    // ====== Control del ciclo de vida: solo anima cuando la pantalla es visible ======

    private void iniciar() {
        if (activo) return;
        activo = true;
        ultimoTiempo = 0;
        postInvalidateOnAnimation();
    }

    private void detener() {
        activo = false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getWindowVisibility() == VISIBLE) iniciar();
    }

    @Override
    protected void onDetachedFromWindow() {
        detener();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) iniciar();
        else detener();
    }
}
