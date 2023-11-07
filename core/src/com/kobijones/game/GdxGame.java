package com.kobijones.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GdxGame extends ApplicationAdapter {
	static final float PIXEL_SIZE = 0.03f;
	static final int PADDLE_HEIGHT = 5;
	static final float PADDLE_SPEED = 0.5f;

	Viewport viewport;

	ShapeRenderer shapeRenderer;

	Vector2 ballSize = new Vector2(PIXEL_SIZE, PIXEL_SIZE);
	Vector2 position = new Vector2(0.0f, 0.0f);
	Vector2 speed = new Vector2(0.5f, 0.5f);

	Vector2 paddleSize = new Vector2(PIXEL_SIZE, PIXEL_SIZE * PADDLE_HEIGHT);
	float paddleY = 0;
	
	@Override
	public void create() {
		viewport = new ExtendViewport(1, 1);
		shapeRenderer = new ShapeRenderer();
	}

	@Override
	public void render() {
		// Move paddle
		boolean movePaddleUp = Gdx.input.isKeyPressed(Input.Keys.W);
		boolean movePaddleDown = Gdx.input.isKeyPressed(Input.Keys.S);
		paddleY = clamp(
			0,
			viewport.getWorldHeight() - paddleSize.y,
			paddleY +
				(movePaddleUp ? PADDLE_SPEED * Gdx.graphics.getDeltaTime() : 0) +
				(movePaddleDown ? -PADDLE_SPEED * Gdx.graphics.getDeltaTime() : 0)
		);

		// Move ball
		position.add(speed.cpy().scl(Gdx.graphics.getDeltaTime()));

		// Check paddle ball collision
		float topDst = position.y - (paddleY + paddleSize.y);
		float bottomDst = paddleY - (position.y + ballSize.y);
		float rightDst = position.x - (0 + paddleSize.x);
		if ((topDst <= 0 && bottomDst <= 0) && rightDst <= 0) {
			if (topDst > Math.max(rightDst, bottomDst)) {
				speed.y = Math.abs(speed.y);
				position.y = paddleY + paddleSize.y;
			} else if (rightDst > Math.max(topDst, bottomDst)) {
				speed.x = Math.abs(speed.x);
				position.x = paddleSize.x;
			} else if (bottomDst > Math.max(topDst, rightDst)) {
				speed.y = -Math.abs(speed.y);
				position.y = paddleY - ballSize.y;
			}
		}

		// Check ball boundary collision
		if (position.x <= 0 - ballSize.x) {
			position = new Vector2(
				viewport.getWorldWidth() / 2 - ballSize.x / 2,
				viewport.getWorldHeight() / 2 - ballSize.y / 2
			);
			speed.x = Math.abs(speed.x);
		} else if (position.x + ballSize.x >= viewport.getWorldWidth()) {
			speed.x = -Math.abs(speed.x);
		}
		if (position.y <= 0) {
			speed.y = Math.abs(speed.y);
		} else if (position.y + ballSize.y >= viewport.getWorldHeight()) {
			speed.y = -Math.abs(speed.y);
		}

		// Render scene

		viewport.apply();

		ScreenUtils.clear(0, 0, 0, 1);
		shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(Color.WHITE);
		shapeRenderer.rect(position.x, position.y, ballSize.x, ballSize.y);
		shapeRenderer.rect(0, paddleY, paddleSize.x, paddleSize.y);
		shapeRenderer.end();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
	}

	@Override
	public void dispose() {
		shapeRenderer.dispose();
	}

	private static float clamp(float min, float max, float val) {
		return Math.min(Math.max(min, val), max);
	}
}
