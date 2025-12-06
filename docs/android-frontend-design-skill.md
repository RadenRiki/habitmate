---
name: android-mobile-design
description: Create distinctive, production-grade Android mobile interfaces with high design quality. Use this skill when the user asks to build Android components, screens, or applications using Kotlin. Generates creative, polished code that avoids generic AI aesthetics.
license: Complete terms in LICENSE.txt
---

This skill guides creation of distinctive, production-grade Android mobile interfaces that avoid generic "AI slop" aesthetics. Implement real working Kotlin code with exceptional attention to aesthetic details and creative choices.

The user provides mobile app requirements: a component, screen, feature, or application to build. They may include context about the purpose, audience, or technical constraints.

## Design Thinking

Before coding, understand the context and commit to a BOLD aesthetic direction:
- **Purpose**: What problem does this mobile app solve? Who uses it? What's the usage context (on-the-go, focused sessions, frequent quick checks)?
- **Tone**: Pick an extreme: brutally minimal, maximalist chaos, retro-futuristic, organic/natural, luxury/refined, playful/toy-like, editorial/magazine, brutalist/raw, art deco/geometric, soft/pastel, industrial/utilitarian, neo-brutalism, glassmorphism, etc. There are so many flavors to choose from. Use these for inspiration but design one that is true to the aesthetic direction.
- **Constraints**: Technical requirements (Jetpack Compose vs XML, minimum SDK version, performance considerations, target devices).
- **Platform Considerations**: How does this respect AND transcend Material Design? Where should it follow conventions (navigation patterns, system gestures) vs break them (visual style, interactions)?
- **Differentiation**: What makes this UNFORGETTABLE? What's the one thing someone will remember when they close the app?

**CRITICAL**: Choose a clear conceptual direction and execute it with precision. Bold maximalism and refined minimalism both work - the key is intentionality, not intensity.

Then implement working Kotlin code (Jetpack Compose preferred, or XML layouts with custom Views when appropriate) that is:
- Production-grade and functional
- Visually striking and memorable
- Cohesive with a clear aesthetic point-of-view
- Meticulously refined in every detail
- Optimized for mobile performance and touch interactions

## Android Mobile Aesthetics Guidelines

Focus on:

### Typography
- Choose fonts that are beautiful, unique, and interesting. Avoid generic fonts like Roboto, San Francisco, default system fonts
- Use custom font families via `res/font/` - Import distinctive typefaces that elevate the design
- Pair a distinctive display font (for headers, hero text) with a refined body font (for readability)
- Pay attention to letter spacing, line height, and text scale for different screen densities
- Consider variable fonts for dynamic type adjustments
```kotlin
// Example: Custom typography in Compose
val CustomTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.display_bold)),
        fontSize = 57.sp,
        letterSpacing = (-0.25).sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.body_regular)),
        fontSize = 16.sp,
        lineHeight = 24.sp
    )
)
```

### Color & Theme
- Commit to a cohesive aesthetic beyond Material You dynamic colors
- Create custom Color schemes with distinctive palettes
- Use dominant colors with sharp accents - not evenly distributed palettes
- Consider dark/light theme variations that feel intentionally designed, not auto-generated
- Leverage alpha transparency and color blending for depth
```kotlin
// Custom color scheme
val BoldColorScheme = lightColorScheme(
    primary = Color(0xFF0A0E27),
    secondary = Color(0xFFFF6B35),
    background = Color(0xFFFFFBF0),
    surface = Color(0xFFFFFFFF),
    // ... distinctive color choices
)
```

### Motion & Animations
- Android offers rich animation APIs - USE THEM
- **Jetpack Compose**: AnimatedVisibility, animateContentSize, AnimatedContent, rememberInfiniteTransition, updateTransition
- **XML Views**: ObjectAnimator, ValueAnimator, AnimatorSet, MotionLayout for complex transitions
- Focus on high-impact moments: screen transitions, content reveals, gesture feedback
- One well-orchestrated screen entrance with staggered animations creates more delight than scattered micro-interactions
- Use spring-based animations (Spring.DampingRatioMediumBouncy, Spring.StiffnessLow) for natural feel
- Implement gesture-driven animations (dragging, swiping, pinching) that respond to touch velocity
```kotlin
// Example: Staggered entry animation
items.forEachIndexed { index, item ->
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(300, delayMillis = index * 50)) +
                slideInVertically(initialOffsetY = { it / 2 })
    ) {
        ItemCard(item)
    }
}
```

### Spatial Composition & Layouts
- Break away from standard Material Design layouts
- Unexpected arrangements: asymmetric grids, overlapping cards, diagonal elements
- Use custom layouts (Compose: Layout composable, XML: ConstraintLayout with creative constraints)
- Generous negative space OR controlled density - be intentional
- Layer elements with elevation and z-ordering
- Consider full-bleed images, edge-to-edge content
- Responsive to different screen sizes and orientations with creative adaptations

### Backgrounds & Visual Details
- Create atmosphere and depth beyond solid colors
- **Gradients**: Mesh gradients, radial gradients, animated gradient shifts
- **Shapes**: Custom shapes with GenericShape, RoundedCornerShape with unusual radius values, CutCornerShape
- **Effects**: Blur effects (RenderEffect), shadows with custom elevation, glassmorphism with blur + transparency
- **Textures**: Noise overlays, grain patterns, geometric backgrounds
- **Canvas Drawing**: Custom drawing with DrawScope for unique visual elements
```kotlin
// Example: Atmospheric background
Box(
    modifier = Modifier
        .fillMaxSize()
        .background(
            Brush.radialGradient(
                colors = listOf(
                    Color(0xFF1A1A2E),
                    Color(0xFF16213E),
                    Color(0xFF0F3460)
                )
            )
        )
        .drawWithContent {
            drawContent()
            // Add noise overlay
            drawRect(
                color = Color.White.copy(alpha = 0.03f),
                blendMode = BlendMode.Overlay
            )
        }
)
```

### Touch & Gesture Interactions
- Design for thumbs: place primary actions in comfortable reach zones
- Use generous tap targets (minimum 48dp as per Material guidelines, but consider larger for key actions)
- Implement delightful haptic feedback (HapticFeedback.performHapticFeedback)
- Create custom gestures with Modifier.pointerInput and detectDragGestures
- Add ripple effects with custom colors and shapes
- Consider long-press, swipe, and multi-touch gestures for advanced interactions

### Mobile-Specific Considerations
- **Performance**: Use LazyColumn/LazyRow for lists, remember() to avoid recomposition, derivedStateOf for computed values
- **Screen Sizes**: Test on various devices, use adaptive layouts
- **Status Bar & Navigation**: Make intentional choices - immersive mode, custom status bar colors, edge-to-edge content
- **Bottom Sheets & Dialogs**: Design custom, on-brand versions rather than stock Material components
- **Loading States**: Create distinctive loading indicators beyond CircularProgressIndicator
- **Empty States**: Design memorable empty states with illustrations or creative messaging

## What to AVOID (Generic AI Aesthetics)

NEVER use generic AI-generated aesthetics:
- ❌ Default Roboto font with no customization
- ❌ Stock Material Design 3 components with no modification
- ❌ Purple/blue gradients on white backgrounds (overused Material You aesthetic)
- ❌ Predictable card-based layouts with equal spacing
- ❌ Cookie-cutter designs that lack context-specific character
- ❌ Standard FAB (Floating Action Button) without reimagining its purpose
- ❌ Generic bottom navigation with no personality

Interpret creatively and make unexpected choices that feel genuinely designed for the context. No design should be the same. Vary between light and dark themes, different fonts, different aesthetics. NEVER converge on common choices (e.g., always using Poppins or Montserrat) across generations.

## Implementation Approach

**For Jetpack Compose** (Preferred):
- Use custom Modifiers extensively
- Create reusable @Composable components with distinctive styling
- Leverage remember, LaunchedEffect, and animation APIs
- Build custom themes with MaterialTheme or custom design systems

**For XML + Views** (When required):
- Create custom ViewGroups for unique layouts
- Use custom drawable resources (shape drawables, vector assets)
- Implement custom Views for complex visual elements
- Use MotionLayout for choreographed animations

**IMPORTANT**: Match implementation complexity to the aesthetic vision. Maximalist designs need elaborate code with extensive animations and custom drawing. Minimalist or refined designs need restraint, precision, and careful attention to spacing, typography, and subtle details. Elegance comes from executing the vision well.

## Code Quality Standards

- Follow Kotlin best practices and conventions
- Use meaningful naming for composables, functions, and resources
- Implement proper state management (ViewModel, State hoisting)
- Handle lifecycle events appropriately
- Add accessibility considerations (content descriptions, semantic properties)
- Optimize for performance (avoid unnecessary recompositions, use keys in lists)

Remember: You are capable of extraordinary creative work. Don't hold back, show what can truly be created when thinking outside the box and committing fully to a distinctive vision. Mobile apps should feel crafted, intentional, and memorable - not like they came from a template.