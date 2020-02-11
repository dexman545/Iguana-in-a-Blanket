package dex.iguanablanket.helpers;

import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldView;

import java.util.Optional;

public abstract class RespawnHelpers {
    public static Optional<Vec3d> findWakeUpPosition(EntityType<?> type, WorldView worldView, BlockPos pos, int index) {
        Direction direction = Direction.NORTH;
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();

        for(int l = 0; l <= 10; ++l) {
            int m = i - direction.getOffsetX() * l - 1;
            int n = k - direction.getOffsetZ() * l - 1;
            int o = m + 2;
            int p = n + 2;

            for(int q = m; q <= o; ++q) {
                for(int r = n; r <= p; ++r) {
                    BlockPos blockPos = worldView.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(q, j, r));
                    Optional<Vec3d> optional = canWakeUpAt(type, worldView, blockPos);
                    if (optional.isPresent()) {
                        return optional;
                    }
                }
            }
        }

        return Optional.empty();
    }

    protected static Optional<Vec3d> canWakeUpAt(EntityType<?> type, WorldView worldView, BlockPos pos) {
        VoxelShape voxelShape = worldView.getBlockState(pos).getCollisionShape(worldView, pos);
        if (voxelShape.getMaximum(Direction.Axis.Y) > 0.4375D) {
            return Optional.empty();
        } else {
            BlockPos.Mutable mutable = new BlockPos.Mutable(pos);

            while(mutable.getY() >= 0 && pos.getY() - mutable.getY() <= 2 && worldView.getBlockState(mutable).getCollisionShape(worldView, mutable).isEmpty()) {
                mutable.setOffset(Direction.DOWN);
            }

            VoxelShape voxelShape2 = worldView.getBlockState(mutable).getCollisionShape(worldView, mutable);
            if (voxelShape2.isEmpty()) {
                return Optional.empty();
            } else {
                double d = (double)mutable.getY() + voxelShape2.getMaximum(Direction.Axis.Y) + 2.0E-7D;
                if ((double)pos.getY() - d > 2.0D) {
                    return Optional.empty();
                } else {
                    float f = type.getWidth() / 2.0F;
                    Vec3d vec3d = new Vec3d((double)mutable.getX() + 0.5D, d, (double)mutable.getZ() + 0.5D);
                    return worldView.doesNotCollide(new Box(vec3d.x - (double)f, vec3d.y, vec3d.z - (double)f, vec3d.x + (double)f, vec3d.y + (double)type.getHeight(), vec3d.z + (double)f)) ? Optional.of(vec3d) : Optional.empty();
                }
            }
        }
    }
}
