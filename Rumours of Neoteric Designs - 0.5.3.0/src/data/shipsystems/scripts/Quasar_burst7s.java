package data.shipsystems.scripts;

import java.awt.Color;

import com.fs.starfarer.api.impl.combat.OrionDeviceStats;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
public class Quasar_burst7s extends OrionDeviceStats {

	public Quasar_burst7s() {
		p = new OrionDeviceParams();
		
		p.bombWeaponId = "nb_bomblauncher";
		p.shapedExplosionColor = new Color(100, 255, 219,90);
		p.shapedExplosionColor = new Color(100, 255, 178,45);
		p.shapedExplosionScatter = 1f;
		
		p.shapedExplosionNumParticles = 300;
		p.shapedExplosionOffset = 35f;
		p.shapedExplosionEndSizeMin = 2f;
		p.shapedExplosionEndSizeMax = 4f;
		p.shapedExplosionMinParticleSize = 70;
		p.shapedExplosionMaxParticleSize = 110;
		p.shapedExplosionMinParticleVel = 100;
		p.shapedExplosionMaxParticleVel = 450;
		p.shapedExplosionArc = 200f;

		p.jitterColor = new Color(100, 255, 201,35);
		p.maxJitterDur = 0.6f;

		p.impactAccel = 7500f;
		p.impactRateMult = 1.25f;

		p.bombFadeInTime = 1f;
		p.bombLiveTime = 0f;
		p.bombSpeed = 0f;
	}
	
	

	@Override
	protected void advanceImpl(float amount, ShipAPI ship, State state, float effectLevel) {
		super.advanceImpl(amount, ship, state, effectLevel);

		if (effectLevel > 0) {
			for (WeaponAPI w : ship.getAllWeapons()) {
				if (w.isDecorative() && w.getSpec().hasTag("quasar7s")) {
					w.setForceFireOneFrame(true);
					w.forceShowBeamGlow();
				}
			}
		}
	}




	@Override
	protected void notifySpawnedExplosionParticles(Vector2f bombLoc) {
		Color c = new Color(100, 255, 178,255);
		float expSize = 0;
		float durFringe = 1f;
		float durFringe2 = 0.75f;
		float dur = 0.75f;
//		durFringe2 = 1;
//		dur = 1;
		//Global.getCombatEngine().addHitParticle(bombLoc, new Vector2f(), expSize, 1f, durFringe, c);
		//Global.getCombatEngine().addHitParticle(bombLoc, new Vector2f(), expSize * 0.67f, 1f, durFringe2, c);
		//Global.getCombatEngine().addHitParticle(bombLoc, new Vector2f(), expSize * 0.33f, 1f, dur, Color.white);

		
	}
	
	
	
}












