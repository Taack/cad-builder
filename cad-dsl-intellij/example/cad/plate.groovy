// Plate comes from the parts jar (parts/, published to the local Maven repository): the runner gets it from the IDE
import org.example.parts.Plate

double length = param('length', 80)

Plate.drilled(length, length / 2, 4, 6).display()
